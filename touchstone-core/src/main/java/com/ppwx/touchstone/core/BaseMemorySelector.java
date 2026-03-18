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

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.ppwx.touchstone.core.BucketUtil.MAPPING_RANGE;

/**
 *
 * 分桶分流操作实现
 * 内部会维护某项实验的所有分组信息，并完成分组指派判断等功能
 *
 * @author chengzhengZhou
 * @date 2023/10/26 15:37
 * @since 1.0.0
 */
@Slf4j
public class BaseMemorySelector implements ISelector, IGroupConfigAware {

    /**
     * 白名单
     */
    private Map<String, Group> whitelist = new HashMap<>(16);

    /**
     * 固定桶映射
     * key范围为0-100
     */
    private Map<Integer, Group> bucketMap = new HashMap<>(MAPPING_RANGE);

    private IGroupTestConfig groupConfig;

    @Override
    public Group select(Object key) {
        if (key == null) {
            return bucketMap.get(0);
        }
        // hit
        if (whitelist.containsKey(key.toString())) {
            return whitelist.get(key.toString());
        }
        return bucketMap.get(BucketUtil.assignBucket(key));
    }

    @Override
    public synchronized void initWithConfig(IGroupTestConfig groupConfig) {
        if (this.groupConfig == null || !Objects.equals(groupConfig.lastModify(), this.groupConfig.lastModify())) {
            if (groupConfig.getGroupBeans() == null) {
                // not happened
                return;
            }

            Map<Integer, Group> map = new HashMap<>(MAPPING_RANGE);
            Map<String, Group> whitelist = new HashMap<>(8);
            groupConfig.getGroupBeans().forEach(group -> {
                List<Integer> assignedBuckets = group.getAssignedBuckets();
                for (Integer bucket : assignedBuckets) {
                    map.put(bucket, group);
                }
                if (group.getWhitelist() != null) {
                    group.getWhitelist().forEach(item -> whitelist.put(item, group));
                }
            });
            this.bucketMap = map;
            this.whitelist = whitelist;

            log.info("Selector reload config:{}", groupConfig);
            this.groupConfig = groupConfig;
        }

    }
}