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