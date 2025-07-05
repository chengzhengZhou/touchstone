/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: SelectorRegistry
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/31 13:52
 * Description: 分组配置自动更新注册器
 */
package com.ppwx.touchstone.core;

/**
 *
 * 实验组选择器注册类
 * 可用于动态注册和获取
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/31 13:52
 * @since 1.0.0
 */
public interface SelectorRegistry {

    /**
     * 注册分组选择器
     *
     * @param namespace
     * @param groupTestName
     * @param selector
     * @return void
     */
    void register(String namespace, String groupTestName, ISelector selector);

    /**
     * 获取分组选择器
     *
     * @param namespace
     * @param groupTestName
     * @return com.ppwx.touchstone.ISelector
     */
    ISelector find(String namespace, String groupTestName);
}