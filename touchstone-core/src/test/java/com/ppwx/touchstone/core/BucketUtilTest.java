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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * BucketUtil测试类
 *
 * @date 2023/10/27 18:26
 * @since 1.0.0
 */
public class BucketUtilTest {

    @Test
    public void testShuffle_isPermutationAndDeterministic() {
        int bucketSize = BucketUtil.MAPPING_RANGE;
        int seed = 1;

        List<Integer> s1 = BucketUtil.shuffle(bucketSize, seed);
        List<Integer> s2 = BucketUtil.shuffle(bucketSize, seed);
        Assert.assertEquals("shuffle should be deterministic for same seed", s1, s2);

        Assert.assertEquals(bucketSize, s1.size());
        Set<Integer> set = new HashSet<>(s1);
        Assert.assertEquals("shuffle should contain unique buckets", bucketSize, set.size());
        Assert.assertTrue("shuffle should contain 0", set.contains(0));
        Assert.assertTrue("shuffle should contain bucketSize-1", set.contains(bucketSize - 1));
    }

    @Test
    public void testAssignBucket_inRange() {
        for (int i = 0; i < 10_000; i++) {
            int b = BucketUtil.assignBucket(i);
            Assert.assertTrue("bucket should be >= 0", b >= 0);
            Assert.assertTrue("bucket should be < 100", b < BucketUtil.MAPPING_RANGE);
        }
    }

    @Test
    public void testAssignBuckets_twoGroups_disjointAndSizeMatchesRate() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);

        Assert.assertEquals(20, groupA.getAssignedBuckets().size());
        Assert.assertEquals(30, groupB.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
    }

    @Test
    public void testAssignBuckets_increaseRate_preservesExistingBuckets() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        List<Integer> originABuckets = groupA.getAssignedBuckets();

        // enlarge A rate
        groupA = new Group("A", 30);
        groupA.assignBuckets(originABuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        Assert.assertEquals(30, groupA.getAssignedBuckets().size());
        Assert.assertTrue(groupA.getAssignedBuckets().containsAll(originABuckets));
        assertDisjointBuckets(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
    }

    @Test
    public void testAssignBuckets_decreaseRate_truncatesToRate() {
        Group groupA = new Group("A", 20);
        BucketUtil.assignBuckets(Collections.singletonList(groupA), 1);
        List<Integer> originBuckets = groupA.getAssignedBuckets();

        Group smaller = new Group("A", 10);
        smaller.assignBuckets(originBuckets);
        BucketUtil.assignBuckets(Collections.singletonList(smaller), 1);
        Assert.assertEquals(10, smaller.getAssignedBuckets().size());

        // should preserve prefix buckets due to subList(0, rate)
        Assert.assertEquals(originBuckets.subList(0, 10), smaller.getAssignedBuckets());
    }

    @Test
    public void testAssignBuckets_toHundred_oneGroupGetsAllBuckets() {
        Group groupA = new Group("A", 100);
        Group groupB = new Group("B", 0);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
        Assert.assertEquals(100, groupA.getAssignedBuckets().size());
        Assert.assertEquals(0, groupB.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testErrorRateWorks() {
        Group groupA = new Group("A", 90);
        Group groupB = new Group("B", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB), 1);
    }

    @Test
    public void testOneGroupWorks() {
        Group groupA = new Group("A", 90);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        Assert.assertEquals(90, groupA.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), null);

        List<Integer> originABuckets = groupA.getAssignedBuckets();
        groupA = new Group("A", 10);
        groupA.assignBuckets(originABuckets);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        Assert.assertEquals(10, groupA.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), null);

        groupA = new Group("A", 100);
        BucketUtil.assignBuckets(Arrays.asList(groupA), 1);
        Assert.assertEquals(100, groupA.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), null);
    }

    @Test
    public void testMultiGroupWorks() {
        Group groupA = new Group("A", 20);
        Group groupB = new Group("B", 20);
        Group groupC = new Group("C", 20);
        Group groupD = new Group("D", 30);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB, groupC, groupD), 1);
        Assert.assertEquals(20, groupA.getAssignedBuckets().size());
        Assert.assertEquals(20, groupB.getAssignedBuckets().size());
        Assert.assertEquals(20, groupC.getAssignedBuckets().size());
        Assert.assertEquals(30, groupD.getAssignedBuckets().size());
        assertDisjointBuckets(groupA.getAssignedBuckets(), groupB.getAssignedBuckets());
        assertDisjointBuckets(groupA.getAssignedBuckets(), groupC.getAssignedBuckets());
        assertDisjointBuckets(groupB.getAssignedBuckets(), groupD.getAssignedBuckets());

        groupA = new Group(groupA, 100);
        groupB = new Group(groupB, 0);
        groupC = new Group(groupC, 0);
        groupD = new Group(groupD, 0);
        BucketUtil.assignBuckets(Arrays.asList(groupA, groupB, groupC, groupD), 1);
        Assert.assertEquals(100, groupA.getAssignedBuckets().size());
        Assert.assertEquals(0, groupB.getAssignedBuckets().size());
        Assert.assertEquals(0, groupC.getAssignedBuckets().size());
        Assert.assertEquals(0, groupD.getAssignedBuckets().size());
    }

    private static void assertDisjointBuckets(List<Integer> a, List<Integer> b) {
        Assert.assertNotNull(a);
        Set<Integer> set = new HashSet<>(a);
        Assert.assertEquals("Buckets in a should be unique", a.size(), set.size());

        if (b == null) {
            return;
        }
        Set<Integer> setB = new HashSet<>(b);
        Assert.assertEquals("Buckets in b should be unique", b.size(), setB.size());
        for (Integer val : set) {
            Assert.assertFalse("Buckets should be disjoint", setB.contains(val));
        }
    }
}