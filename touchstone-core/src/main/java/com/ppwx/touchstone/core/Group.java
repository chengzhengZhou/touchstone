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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 实验分组对象
 *
 * @date 2023/10/26 13:42
 * @since 1.0.0
 */
public class Group {

    public interface MetaInfo {
        /**
         * 分组编号，需要唯一
         *
         * @return java.lang.String
         */
        String getGroupNo();

        /**
         * 分流比例 0-100
         *
         * @return int
         */
        int getRate();
    }

    /**
     * 元数据信息
     */
    public MetaInfo metaInfo;

    /**
     * 分桶下标
     */
    private List<Integer> assignedBuckets;

    /**
     * 白名单用户
     * 限制不超过5个
     */
    private List<String> whitelist;

    /**
     * 默认构造器
     *
     * @param groupNo
     * @param rate
     * @return
     */
    public Group(String groupNo, int rate) {
        this(groupNo, rate, new ArrayList<>());
    }

    /**
     * 全参构造
     *
     * @param groupNo
     * @param rate
     * @param buckets
     * @return
     */
    public Group(String groupNo, int rate, List<Integer> buckets) {
        this.metaInfo = new MetaInfo() {
            @Override
            public String getGroupNo() {
                return groupNo;
            }

            @Override
            public int getRate() {
                return rate;
            }
        };
        this.assignedBuckets = buckets;
    }

    /**
     * 从现有的group构建一个新比率的组
     *
     * @param origin
     * @param rate
     * @return
     */
    public Group(Group origin, int rate) {
        this(origin.getMetaInfo().getGroupNo(), rate, origin.getAssignedBuckets());
    }

    /**
     * 指派桶编号
     * 每个桶表1个百分点，指派完毕后累加值满足rate值
     *
     * @param bucket
     * @return void
     */
    void assignBucket(int bucket) {
        assignedBuckets.add(bucket);
    }

    /**
     * 批量指派桶
     *
     * @param buckets
     * @return void
     */
    void assignBuckets(List<Integer> buckets) {
        this.assignedBuckets.addAll(buckets);
    }

    /**
     * 清除分桶信息
     *
     * @param
     * @return java.util.List<java.lang.Integer>
     */
    List<Integer> clearBucket() {
        List<Integer> buckets = this.assignedBuckets;
        this.assignedBuckets = new ArrayList<>();
        return buckets;
    }

    /**
     * 获取当前实验组的分桶信息
     *
     * @param
     * @return java.util.List<java.lang.Integer>
     */
    public List<Integer> getAssignedBuckets() {
        return new ArrayList<>(this.assignedBuckets);
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public String toString() {
        return "Group{" +
                "metaInfo=" + metaInfo == null ? null : (metaInfo.getGroupNo() + ":" + metaInfo.getRate()) +
                ", assignedBuckets=" + assignedBuckets +
                ", whiteList=" + whitelist +
                '}';
    }
}