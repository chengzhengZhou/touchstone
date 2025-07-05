/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: BucketUtil
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/27 16:58
 * Description: 分桶工具
 */
package com.ppwx.touchstone.core;

import cn.hutool.core.util.HashUtil;

import java.util.*;

/**
 *
 * 分桶工具
 * 提供了分桶打散、分桶指派等算法
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
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
     * 分桶指派
     *
     * @param key
     * @return int
     */
    public static int assignBucket(Object key) {
        return hash(key.toString()) % MAPPING_RANGE;
    }

    /**
     * 对实验计划下的分组指派分桶
     * 支持增量修改，扩大比例的情况下能保证不破坏原本分组映射
     * 若多个分组情况下调整比例可能会导致分组用户发生变更
     *
     * @param groupList
     * @return void
     */
    public static void assignBuckets(List<Group> groupList, int rndSeed) {
        validGroup(groupList);
        List<Integer> shuffle = shuffle(MAPPING_RANGE, rndSeed);
        Map<Group, Integer> needFillGroup = new HashMap<>(8);

        for (Group group : groupList) {
            int rate = group.getMetaInfo().getRate();
            List<Integer> ownedBuckets = group.clearBucket();
            int diff = rate - ownedBuckets.size();
            if (diff > 0) {
                needFillGroup.put(group, diff);
            } else if (diff < 0) {
                ownedBuckets = ownedBuckets.subList(0, rate);
            }
            // remove already assigned
            shuffle.removeAll(ownedBuckets);
            group.assignBuckets(ownedBuckets);
        }

        Iterator<Map.Entry<Group, Integer>> iterator = needFillGroup.entrySet().iterator();
        int idx = 0;
        int remain = shuffle.size();
        while (iterator.hasNext() && remain > idx) {
            Map.Entry<Group, Integer> next = iterator.next();
            Group group = next.getKey();
            Integer size = next.getValue();
            List<Integer> buckets = group.clearBucket();
            buckets.addAll(shuffle.subList(idx, idx + size));
            group.assignBuckets(buckets);
            idx += size;
        }

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