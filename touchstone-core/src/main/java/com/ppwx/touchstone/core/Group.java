/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: Group
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/26 13:42
 * Description: 实验分组对象
 */
package com.ppwx.touchstone.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 实验分组对象
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
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