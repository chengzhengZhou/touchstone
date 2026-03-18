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
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 *
 * GroupConfig测试类
 *
 * @author chengzhengZhou
 * @date 2023/10/30 17:11
 * @since 1.0.0
 */
@Slf4j
public class GroupConfigTest {

    @Test
    public void testParseGroupConfigWorks() {
        GroupConfig config = new GroupConfig();
        config.setTestGroups("0|A|10|0,1,2,3,4,5,6,7,8,9;");
        config.parseGroupConfig();
        log.info("groups:{}", config.getGroupBeans());
    }

    @Test
    public void testNormalizeGroupWorks() {
        GroupConfig config = new GroupConfig();
        config.setGroupBeans(Collections.singletonList(new Group("A", 10, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))));
        config.normalizeGroup();
        log.info("groupConfigs:{}", config.getTestGroups());
    }

}