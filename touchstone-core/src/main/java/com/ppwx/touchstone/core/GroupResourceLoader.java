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
import java.util.Set;

/**
 * 实验组资源加载器
 *
 * @date 2023/10/26 23:47
 * @since 1.0.0
 */
public interface GroupResourceLoader {

    /**
     * 加载所有实验计划配置
     *
     * @param
     * @return java.util.List<com.ppwx.touchstone.IGroupTestConfig>
     */
    List<IGroupTestConfig> loadAll();

    /**
     * 获取配置的版本
     *
     * @param namespace 命名空间
     * @return java.lang.Long
     */
    Long getVersion(String namespace);

    /**
     * 获取命名空间下的所有实验配置
     *
     * @param namespace
     * @return java.util.List<com.ppwx.touchstone.IGroupTestConfig>
     */
    List<IGroupTestConfig> loadAll(String namespace);

    /**
     * 获取命名空间
     * @return java.util.Set
     */
    Set<String> getNamespaces();
}
