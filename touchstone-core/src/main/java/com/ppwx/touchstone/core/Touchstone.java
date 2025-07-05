/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: Touchstone
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/26 16:20
 * Description: 试金石入口类
 */
package com.ppwx.touchstone.core;

/**
 *
 * 试金石入口类
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 16:20
 * @since 1.0.0
 */
public class Touchstone {

    /**
     * 兜底分组
     */
    public static final Group.MetaInfo BASE_GROUP = new Group.MetaInfo() {
        @Override
        public String getGroupNo() {
            return "BASE";
        }

        @Override
        public int getRate() {
            return 0;
        }
    };

    /**
     * 分组实验组注册器
     */
    private static SelectorRegistry selectorRegistry;

    static void setSelectorRegistry(SelectorRegistry selectorRegistry) {
        Touchstone.selectorRegistry = selectorRegistry;
    }

    /**
     * 实验分流分组
     * 需要指定实验计划名称，然后传入关键字段用于获取实验分组结果
     *
     * @param namespace
     * @param groupName
     * @param key
     * @return com.ppwx.touchstone.Group.MetaInfo
     */
    public static final Group.MetaInfo test(String namespace, String groupName, Object key) {
        Group group = null;
        ISelector selector = selectorRegistry.find(namespace, groupName);
        if (selector != null) {
            group = selector.select(key);
        }
        return group == null ? BASE_GROUP : group.getMetaInfo();
    }
}