/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: BucketUtilTest
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/27 18:26
 * Description: BucketUtil测试类
 */
package com.ppwx.touchstone.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * BucketUtil测试类
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/27 18:26
 * @since 1.0.0
 */
@Slf4j
public class BucketUtilTest {

    @Test
    public void testAssignBucketsWorks() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());
    }

    @Test
    public void testReassignBucketWorks() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());

        // enlarge A rate
        List<Integer> originABuckets = groupA.getAssignedBuckets();
        groupA = new Group("A", 30);
        groupA.assignBuckets(originABuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());

        // smaller A rate
        originABuckets = groupA.getAssignedBuckets();
        groupA = new Group("A", 20);
        groupA.assignBuckets(originABuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());

        // enlarge A rate and enlarge B rate
        originABuckets = groupA.getAssignedBuckets();
        List<Integer> originBBuckets = groupB.getAssignedBuckets();
        groupA = new Group("A", 25);
        groupB = new Group("B", 35);
        groupA.assignBuckets(originABuckets);
        groupB.assignBuckets(originBBuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());

        // smaller A rate and smaller B rate
        originABuckets = groupA.getAssignedBuckets();
        originBBuckets = groupB.getAssignedBuckets();
        groupA = new Group("A", 10);
        groupB = new Group("B", 5);
        groupA.assignBuckets(originABuckets);
        groupB.assignBuckets(originBBuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());

        // to hundred
        originABuckets = groupA.getAssignedBuckets();
        originBBuckets = groupB.getAssignedBuckets();
        groupA = new Group("A", 100);
        groupB = new Group("B", 0);
        groupA.assignBuckets(originABuckets);
        groupB.assignBuckets(originBBuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorRateWorks() {
        Group groupA = new Group("A", 90);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());
    }

    @Test
    public void testOneGroupWorks() {
        Group groupA = new Group("A", 90);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        checkRepeat(groupA.getAssignedBuckets(), null);
        log.info("groupA:{}", groupA.getAssignedBuckets());

        List<Integer> originABuckets = groupA.getAssignedBuckets();
        groupA = new Group("A", 10);
        groupA.assignBuckets(originABuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        checkRepeat(groupA.getAssignedBuckets(), null);
        log.info("groupA:{}", groupA.getAssignedBuckets());

        groupA = new Group("A", 100);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        checkRepeat(groupA.getAssignedBuckets(), null);
        log.info("groupA:{}", groupA.getAssignedBuckets());
    }

    @Test
    public void testMultiGroupWorks() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 20);
        Group groupC = new Group("C", 20);
        Group groupD = new Group("D", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB, groupC, groupD), 1);
        checkRepeat(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        checkRepeat(groupA.getAssignedBuckets(), groupC.getAssignedBuckets());
        checkRepeat(groupB.getAssignedBuckets(), groupD.getAssignedBuckets());
        log.info("groupA:{}", groupA.getAssignedBuckets());
        log.info("groupB:{}", groupB.getAssignedBuckets());
        log.info("groupC:{}", groupC.getAssignedBuckets());
        log.info("groupD:{}", groupD.getAssignedBuckets());

        groupA = new Group(groupA, 100);
        groupB = new Group(groupB, 0);
        groupC = new Group(groupC, 0);
        groupD = new Group(groupD, 0);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB, groupC, groupD), 1);
        checkRepeat(groupA.getAssignedBuckets(), null);
        log.info("groupA:{}，size:{}", groupA.getAssignedBuckets(), groupA.getAssignedBuckets().size());
        log.info("groupB:{}", groupB.getAssignedBuckets());
        log.info("groupC:{}", groupC.getAssignedBuckets());
        log.info("groupD:{}", groupD.getAssignedBuckets());
    }

    private void checkRepeat(List<Integer> a, List<Integer> b) {
        Set<Integer> set = new HashSet<>();
        for (Integer val : a) {
            Assert.assertFalse(set.contains(val));
            set.add(val);
        }

        if (b != null) {
            set = new HashSet<>();
            for (Integer val : b) {
                Assert.assertFalse(set.contains(val));
                set.add(val);
            }

            for (Integer val : a) {
                Assert.assertFalse(b.contains(val));
            }
        }

    }
}