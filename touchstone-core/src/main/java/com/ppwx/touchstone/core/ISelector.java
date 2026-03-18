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
 * 分桶分流操作接口
 * 一般定义一项分流计划时需要设置好对应的组及相关比例，后续请求时会根据特定key固定落到某个桶中
 *
 * @date 2023/10/26 13:58
 * @since 1.0.0
 */
public interface ISelector {

    /**
     * 为当前key对象选择一个组
     *
     * @param key 用于决定所落的桶，尽量不要让key为空值
     * @return com.ppwx.touchstone.Group
     */
    Group select(Object key);

}