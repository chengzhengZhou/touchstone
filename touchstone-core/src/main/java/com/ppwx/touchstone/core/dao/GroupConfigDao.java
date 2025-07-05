/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: GroupConfigDao
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/30 17:35
 * Description: 试金石元数据增删改
 */
package com.ppwx.touchstone.core.dao;

import com.ppwx.touchstone.core.domain.GroupConfig;

import java.util.List;

/**
 *
 * 试金石元数据增删改
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/30 17:35
 * @since 1.0.0
 */
public interface GroupConfigDao {

    /**
     * 保存
     *
     * @param config
     * @return void
     */
    void save(GroupConfig config);

    /**
     * 删除
     *
     * @param config
     * @return int
     */
    int delete(GroupConfig config);

    /**
     * 根据命名空间和名称获取分组实验配置
     *
     * @param namespace
     * @param testName
     * @return com.ppwx.touchstone.domain.GroupConfig
     */
    GroupConfig getGroupConfig(String namespace, String testName);

    /**
     * 获取所有的
     *
     * @param namespace
     * @return java.util.List<com.ppwx.touchstone.domain.GroupConfig>
     */
    List<GroupConfig> listAllByNamespace(String namespace);

    /**
     * 获取命名空间版本
     *
     * @param namespace
     * @return java.lang.Long
     */
    Long getVersionByNamespace(String namespace);

    /**
     * 获取所有的命名空间
     *
     * @param
     * @return java.util.List<java.lang.String>
     */
    List<String> listAllNamespace();
}