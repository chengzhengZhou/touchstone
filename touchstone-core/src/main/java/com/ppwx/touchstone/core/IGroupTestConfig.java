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

import java.util.List;

/**
 * 分组实验计划配置
 *
 * @date 2023/10/26 15:49
 * @since 1.0.0
 */
public interface IGroupTestConfig {

    /**
     * 配置版本号
     */
    String VER_0 = "0";

    /**
     * 配置项分隔符
     */
    String GROUP_SEPARATOR = "|";

    /**
     * 分组命名空间
     *
     * @return java.lang.String
     */
    String namespace();

    /**
     * 分组实验名称
     *
     * @return java.lang.String
     */
    String groupTestName();

    /**
     * 获取实验计划下分组信息
     *
     * @return java.util.List<com.ppwx.touchstone.Group.MetaInfo>
     */
    List<Group> getGroupBeans();

    /**
     * 最新更新时间
     * 若更新时间大于数据时间则需要执行相关变更操作
     *
     * @return java.lang.Long
     */
    Long lastModify();

}
