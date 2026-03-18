/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright (c) 2023-2026 chengzhengZhou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ppwx.touchstone.core;

import com.ppwx.touchstone.core.domain.GroupConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 *
 * BaseSelector测试类
 *
 * @date 2023/10/27 14:24
 * @since 1.0.0
 */
public class BaseSelectorTest {

    @Test
    public void testSelect_nullKey_returnsBucket0Group() {
        Group groupA = new Group("A", 1, Arrays.asList(0));
        Group groupB = new Group("B", 1, Arrays.asList(1));
        GroupConfig config = new GroupConfig();
        config.setTestName("测试1");
        config.setGroupBeans(Arrays.asList(groupA, groupB));

        BaseMemorySelector selector = new BaseMemorySelector();
        selector.initWithConfig(config);

        Assert.assertSame(groupA, selector.select(null));
    }

    @Test
    public void testSelect_whitelistKey_returnsWhitelistedGroup() {
        Group groupA = new Group("A", 1, Arrays.asList(0));
        Group groupB = new Group("B", 1, Arrays.asList(1));
        groupB.setWhitelist(Arrays.asList("user-1"));

        GroupConfig config = new GroupConfig();
        config.setTestName("测试1");
        config.setGroupBeans(Arrays.asList(groupA, groupB));

        BaseMemorySelector selector = new BaseMemorySelector();
        selector.initWithConfig(config);

        Assert.assertSame(groupB, selector.select("user-1"));
    }

    @Test
    public void testSelect_bucketKey_returnsExpectedGroup() {
        Group groupA = new Group("A", 1, Arrays.asList(0));
        Group groupB = new Group("B", 1, Arrays.asList(1));

        GroupConfig config = new GroupConfig();
        config.setTestName("测试1");
        config.setGroupBeans(Arrays.asList(groupA, groupB));

        BaseMemorySelector selector = new BaseMemorySelector();
        selector.initWithConfig(config);

        int keyForBucket1 = findIntKeyForBucket(1);
        Assert.assertSame(groupB, selector.select(keyForBucket1));
    }

    private static int findIntKeyForBucket(int bucket) {
        for (int i = 0; i < 1_000_000; i++) {
            if (BucketUtil.assignBucket(i) == bucket) {
                return i;
            }
        }
        Assert.fail("Unable to find key for bucket=" + bucket);
        return -1;
    }
}