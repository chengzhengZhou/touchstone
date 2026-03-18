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

/**
 *
 * 项目一般同时存在多个实验计划
 * 定义该接口可便于{@link ISelector}容器能感知到分组配置的变更
 *
 * @author chengzhengZhou
 * @date 2023/10/26 15:41
 * @since 1.0.0
 */
public interface IGroupConfigAware {

    /**
     * 实现该方法并用于配置更新时更新对应的算法参数
     *
     * @param groupConfig
     * @return void
     */
    void initWithConfig(IGroupTestConfig groupConfig);

}