/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: BaseSelectorTest
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/27 14:24
 * Description: BaseSelector测试类
 */
package com.ppwx.touchstone.core;

import com.ppwx.touchstone.core.domain.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * BaseSelector测试类
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/27 14:24
 * @since 1.0.0
 */
@Slf4j
public class BaseSelectorTest {

    @Test
    public void testBaseSelectorWorks() {
        Group groupA = new Group("A", 30);
        Group groupB = new Group("B", 60);
        for (int i = 0; i < 90; i++) {
            if (i < 30) {
                groupA.assignBucket(i);
            } else {
                groupB.assignBucket(i);
            }
        }

        GroupConfig config = new GroupConfig();
        config.setTestName("测试1");
        config.setGroupBeans(Arrays.asList(groupA, groupB));

        BaseMemorySelector selector = new BaseMemorySelector();
        selector.initWithConfig(config);

        Map<Group, AtomicInteger> count = new HashMap<>(2);
        count.put(groupA, new AtomicInteger(0));
        count.put(groupB, new AtomicInteger(0));
        count.put(null, new AtomicInteger());
        for (int i = 0; i < 100; i++) {
            count.get(selector.select(i)).incrementAndGet();
        }
        log.info("GroupA:{}", count.get(groupA));
        log.info("GroupB:{}", count.get(groupB));
        log.info("null:{}", count.get(null));
    }

    @Test
    public void testRandomBucketWorks() {
        Random rnd = new Random(1);
        int size = 100;
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

        Arrays.stream(arr).forEach(System.out::println);
    }

}