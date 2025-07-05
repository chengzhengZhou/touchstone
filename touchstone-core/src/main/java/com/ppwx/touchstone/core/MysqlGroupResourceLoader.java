/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: MysqlGroupResourceLoader
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/29 21:56
 * Description: 从数据库中加载实验组配置
 */
package com.ppwx.touchstone.core;

import com.ppwx.touchstone.core.dao.DefaultGroupConfigDao;
import com.ppwx.touchstone.core.domain.GroupConfig;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * 从数据库中加载实验组配置
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/29 21:56
 * @since 1.0.0
 */
public class MysqlGroupResourceLoader implements GroupResourceLoader {

    /**
     * 数据持久化
     */
    private DefaultGroupConfigDao groupConfigDao;

    /**
     * 支持的命名空间
     */
    private final Set<String> namespaces;

    /**
     * 默认构造器
     *
     * @param namespaces
     * @return
     */
    public MysqlGroupResourceLoader(Set<String> namespaces) {
        this.namespaces = namespaces;
    }

    public void setGroupConfigDao(DefaultGroupConfigDao groupConfigDao) {
        this.groupConfigDao = groupConfigDao;
    }

    @Override
    public List<IGroupTestConfig> loadAll() {
        List<IGroupTestConfig> configs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(namespaces)) {
            namespaces.forEach(namespace -> configs.addAll(loadAll(namespace)));
        }
        return configs;
    }

    @Override
    public Long getVersion(String namespace) {
        return groupConfigDao.getVersionByNamespace(namespace);
    }

    @Override
    public List<IGroupTestConfig> loadAll(String namespace) {
        List<GroupConfig> groupConfigs = groupConfigDao.listAllByNamespace(namespace);
        groupConfigs.forEach(GroupConfig::parseGroupConfig);
        return new ArrayList<>(groupConfigs);
    }

    @Override
    public Set<String> getNamespaces() {
        return this.namespaces;
    }
}