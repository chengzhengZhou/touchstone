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

import cn.hutool.core.util.HashUtil;

import java.util.*;

/**
 *
 * 分桶工具
 * 提供了分桶打散、分桶指派等算法
 *
 * @date 2023/10/27 16:58
 * @since 1.0.0
 */
public final class BucketUtil {

    /**
     * 比例100
     */
    public static final int MAPPING_RANGE = 100;

    private BucketUtil() {

    }

    /**
     * hash散列
     *
     * @param key
     * @return int
     */
    public static int hash(String key) {
        return HashUtil.fnvHash(key);
    }

    /**
     * 生成bucketSize个桶，并随机打散
     *
     * @param bucketSize 桶的数量
     * @param rndSeed 随机数
     * @return int[]
     */
    public static List<Integer> shuffle(int bucketSize, int rndSeed) {
        Random rnd = new Random(rndSeed);
        int size = bucketSize;
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }

        // Shuffle array
        for (int i = size; i > 1; i--) {
            int a = i -1;
            int b = rnd.nextInt(i);
            int tmp = arr[a];
            arr[a] = arr[b];
            arr[b] = tmp;
        }

        List<Integer> list = new ArrayList<>(size);
        Arrays.stream(arr).forEach(list::add);
        return list;
    }

    /**
     * 对实验计划下的分组指派分桶，Stable Rebalance（最大粘性）
     * 1.先保留再补齐：保证“先保旧桶”，迁移数量最小
     * 2.缩容时用稳定打分：避免直接截断受历史操作差异影响
     * 3.扩容时采取确定性分配：把 桶池子 排序，再按固定组顺序分配
     *
     * @param groupList 实验分组列表
     * @param rndSeed 随机数种子
     * @return void
     */
    public static void assignBuckets(List<Group> groupList, int rndSeed) {
        validGroup(groupList);
        List<Group> sortedGroups = new ArrayList<>(groupList);
        sortedGroups.sort(Comparator.comparing(group -> group.getMetaInfo().getGroupNo()));

        List<Integer> baseShuffle = shuffle(MAPPING_RANGE, rndSeed);
        Map<Integer, Integer> bucketOrder = new HashMap<>(MAPPING_RANGE);
        for (int i = 0; i < baseShuffle.size(); i++) {
            bucketOrder.put(baseShuffle.get(i), i);
        }

        Map<Group, List<Integer>> retainedBuckets = new IdentityHashMap<>(sortedGroups.size());
        Map<Group, Integer> needFill = new IdentityHashMap<>(sortedGroups.size());
        Set<Integer> reserved = new HashSet<>(MAPPING_RANGE);

        for (Group group : sortedGroups) {
            int rate = group.getMetaInfo().getRate();
            List<Integer> oldBuckets = normalizeBuckets(group.clearBucket());
            int keepSize = Math.min(rate, oldBuckets.size());
            List<Integer> keep = selectRetainedBuckets(
                    group.getMetaInfo().getGroupNo(), oldBuckets, keepSize, rndSeed, bucketOrder);
            retainedBuckets.put(group, keep);
            needFill.put(group, rate - keep.size());
            reserved.addAll(keep);
        }

        List<Integer> freeBuckets = new ArrayList<>(MAPPING_RANGE - reserved.size());
        for (int bucket = 0; bucket < MAPPING_RANGE; bucket++) {
            if (!reserved.contains(bucket)) {
                freeBuckets.add(bucket);
            }
        }
        freeBuckets.sort(Comparator.comparingInt(bucketOrder::get));

        int cursor = 0;
        for (Group group : sortedGroups) {
            int need = needFill.get(group);
            List<Integer> finalBuckets = new ArrayList<>(retainedBuckets.get(group));
            if (need > 0) {
                finalBuckets.addAll(freeBuckets.subList(cursor, cursor + need));
                cursor += need;
            }
            finalBuckets.sort(Comparator.comparingInt(bucketOrder::get));
            group.assignBuckets(finalBuckets);
        }

    }

    private static List<Integer> normalizeBuckets(List<Integer> buckets) {
        Set<Integer> dedup = new LinkedHashSet<>();
        for (Integer bucket : buckets) {
            if (bucket != null && bucket >= 0 && bucket < MAPPING_RANGE) {
                dedup.add(bucket);
            }
        }
        return new ArrayList<>(dedup);
    }
    
    /**
     * 选择保留的桶
     * @param groupNo 分组编号
     * @param ownedBuckets 拥有的桶
     * @param keepSize 保留的桶数量
     * @param rndSeed 随机数种子
     * @param bucketOrder 桶顺序
     * @return 保留的桶
     */
    private static List<Integer> selectRetainedBuckets(String groupNo, List<Integer> ownedBuckets, int keepSize,
                                                       int rndSeed, Map<Integer, Integer> bucketOrder) {
        if (keepSize <= 0) {
            return new ArrayList<>();
        }
        if (keepSize >= ownedBuckets.size()) {
            List<Integer> result = new ArrayList<>(ownedBuckets);
            result.sort(Comparator.comparingInt(bucketOrder::get));
            return result;
        }

        List<Integer> sorted = new ArrayList<>(ownedBuckets);
        sorted.sort((a, b) -> {
            int scoreA = stickyScore(groupNo, a, rndSeed);
            int scoreB = stickyScore(groupNo, b, rndSeed);
            int scoreCompare = Integer.compareUnsigned(scoreB, scoreA);
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Integer.compare(bucketOrder.get(a), bucketOrder.get(b));
        });

        List<Integer> result = new ArrayList<>(sorted.subList(0, keepSize));
        result.sort(Comparator.comparingInt(bucketOrder::get));
        return result;
    }

    private static int stickyScore(String groupNo, int bucket, int rndSeed) {
        return HashUtil.fnvHash(groupNo + "#" + bucket + "#" + rndSeed);
    }

    /**
     * 重新分桶时需要验证rate累加值是否溢出
     *
     * @param groups
     * @return void
     */
    private static void validGroup(List<Group> groups) {
        int sum = groups.stream().mapToInt(group -> group.getMetaInfo().getRate()).sum();
        if (sum > MAPPING_RANGE) {
            throw new IllegalArgumentException("累计rate溢出，当前值：" + sum);
        }
    }
}
