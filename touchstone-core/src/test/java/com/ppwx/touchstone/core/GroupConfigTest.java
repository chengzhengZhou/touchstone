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
import java.util.Collections;
import java.util.List;

/**
 *
 * GroupConfig测试类
 *
 * @date 2023/10/30 17:11
 * @since 1.0.0
 */
public class GroupConfigTest {

    @Test
    public void testNormalizeThenParse_roundTrip() {
        // given
        Group groupA = new Group("A", 2, Arrays.asList(0, 1));
        groupA.setWhitelist(Arrays.asList("u1", "u2"));
        Group groupB = new Group("B", 1, Collections.singletonList(2));

        GroupConfig config = new GroupConfig();
        config.setNamespace("ns");
        config.setTestName("t1");
        config.setGroupBeans(Arrays.asList(groupA, groupB));

        // when
        config.normalizeGroup();
        String testGroups = config.getTestGroups();
        String whitelist = config.getWhitelist();

        GroupConfig parsed = new GroupConfig();
        parsed.setTestGroups(testGroups);
        parsed.setWhitelist(whitelist);
        parsed.parseGroupConfig();

        // then
        List<Group> groups = parsed.getGroupBeans();
        Assert.assertNotNull(groups);
        Assert.assertEquals(2, groups.size());

        Group parsedA = groups.stream()
                .filter(g -> "A".equals(g.getMetaInfo().getGroupNo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing group A"));
        Assert.assertEquals(2, parsedA.getMetaInfo().getRate());
        Assert.assertEquals(Arrays.asList(0, 1), parsedA.getAssignedBuckets());
        Assert.assertEquals(Arrays.asList("u1", "u2"), parsedA.getWhitelist());

        Group parsedB = groups.stream()
                .filter(g -> "B".equals(g.getMetaInfo().getGroupNo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("missing group B"));
        Assert.assertEquals(1, parsedB.getMetaInfo().getRate());
        Assert.assertEquals(Collections.singletonList(2), parsedB.getAssignedBuckets());
        Assert.assertNull(parsedB.getWhitelist());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeGroup_emptyGroups_throws() {
        GroupConfig config = new GroupConfig();
        config.setGroupBeans(Collections.emptyList());
        config.normalizeGroup();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseGroupConfig_duplicateGroupNo_throws() {
        GroupConfig config = new GroupConfig();
        // A duplicated
        config.setTestGroups("0|A|1|0;0|A|1|1;");
        config.parseGroupConfig();
    }
}