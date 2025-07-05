/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: GroupConfigTest
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/30 17:11
 * Description: GroupConfig测试类
 */
package com.ppwx.touchstone.core;

import com.ppwx.touchstone.core.domain.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 *
 * GroupConfig测试类
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/30 17:11
 * @since 1.0.0
 */
@Slf4j
public class GroupConfigTest {

    @Test
    public void testParseGroupConfigWorks() {
        GroupConfig config = new GroupConfig();
        config.setTestGroups("0|A|10|0,1,2,3,4,5,6,7,8,9;");
        config.parseGroupConfig();
        log.info("groups:{}", config.getGroupBeans());
    }

    @Test
    public void testNormalizeGroupWorks() {
        GroupConfig config = new GroupConfig();
        config.setGroupBeans(Collections.singletonList(new Group("A", 10, Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))));
        config.normalizeGroup();
        log.info("groupConfigs:{}", config.getTestGroups());
    }

}