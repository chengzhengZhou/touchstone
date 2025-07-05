/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: TouchstoneControllerEndpoint
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/11/1 14:32
 * Description:
 */
package com.ppwx.touchstone.core.service;

import cn.hutool.core.util.RandomUtil;
import com.ppwx.touchstone.core.BucketUtil;
import com.ppwx.touchstone.core.Group;
import com.ppwx.touchstone.core.RefreshGroupResourceEvent;
import com.ppwx.touchstone.core.Touchstone;
import com.ppwx.touchstone.core.dao.GroupConfigDao;
import com.ppwx.touchstone.core.domain.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 试金石增删改
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/11/01 14:32
 * @since 1.0.0
 */
@Slf4j
public class TouchstoneService implements ApplicationEventPublisherAware {

    private GroupConfigDao groupConfigDao;

    private ApplicationEventPublisher publisher;

    public void setGroupConfigDao(GroupConfigDao groupConfigDao) {
        this.groupConfigDao = groupConfigDao;
    }

    /**
     * 获取所有的命名空间
     *
     * @param
     * @return java.util.List<java.lang.String>
     */
    public List<String> namespaces() {
        return groupConfigDao.listAllNamespace();
    }

    /**
     * 获取命名空间下的所有分组配置
     *
     * @param namespace
     * @return java.util.List<com.ppwx.touchstone.domain.GroupConfig>
     */
    public List<GroupConfig> groups(String namespace) {
        return groupConfigDao.listAllByNamespace(namespace);
    }

    /**
     * 删除实验组
     *
     * @param id
     * @return java.lang.Integer
     */
    public Integer delete(Long id) {
        GroupConfig groupConfig = new GroupConfig();
        groupConfig.setId(id);
        return groupConfigDao.delete(groupConfig);
    }

    /**
     * 保存实验组配置
     *
     * @param namespace
     * @param groupName
     * @return void
     */
    public GroupConfig save(String namespace, String groupName, List<? extends Group.MetaInfo> groupForms) {
        GroupConfig groupConfig = groupConfigDao.getGroupConfig(namespace, groupName);
        boolean isUpdate = (groupConfig != null);

        List<Group> groups = groupForms.stream()
                .map(meta -> new Group(meta.getGroupNo(), meta.getRate())).collect(Collectors.toList());
        int rndSeed;
        GroupConfig newGroupConfig = new GroupConfig();
        if (isUpdate) {
            groupConfig.parseGroupConfig();
            List<Group> oldGroups = groupConfig.getGroupBeans();
            // reset white list
            if (oldGroups != null) {
                for (Group group : groups) {
                    Optional<Group> first = oldGroups.stream()
                            .filter(g -> Objects.equals(g.metaInfo.getGroupNo(), group.metaInfo.getGroupNo())).findFirst();
                    group.setWhitelist(first.map(Group::getWhitelist).orElse(null));
                }
            }
            newGroupConfig.setId(groupConfig.getId());
            rndSeed = groupConfig.getRndSeed();
        } else {
            rndSeed = RandomUtil.getRandom().nextInt();
        }
        BucketUtil.assignBuckets(groups, rndSeed);
        newGroupConfig.setNamespace(namespace);
        newGroupConfig.setTestName(groupName);
        newGroupConfig.setGroupBeans(groups);
        newGroupConfig.setRndSeed(rndSeed);
        groupConfigDao.save(newGroupConfig);
        return newGroupConfig;
    }

    /**
     * 实验组加白
     * 每组限制10个白名单
     *
     * @param namespace
     * @param groupName
     * @param whitelistMap
     * @return com.ppwx.touchstone.core.domain.GroupConfig
     */
    public GroupConfig addWhitelist(String namespace, String groupName, Map<String, List<String>> whitelistMap) {
        int max = 10;
        GroupConfig groupConfig = groupConfigDao.getGroupConfig(namespace, groupName);
        Assert.notNull(groupConfig, "not exists group :" + namespace + "." + groupName);
        groupConfig.parseGroupConfig();
        List<Group> groupBeans = groupConfig.getGroupBeans();
        groupBeans.forEach(group -> {
            List<String> list = whitelistMap.get(group.metaInfo.getGroupNo());
            if (list == null) {
                group.setWhitelist(null);
            } else {
                group.setWhitelist(list.size() > max ? list.subList(0, max) : list);
            }
        });
        groupConfigDao.save(groupConfig);
        return groupConfig;
    }

    /**
     * 实验分组结果
     *
     * @param namespace
     * @param groupName
     * @param key
     * @return com.ppwx.touchstone.core.Group.MetaInfo
     */
    public Group.MetaInfo test(String namespace, String groupName, String key) {
        return Touchstone.test(namespace, groupName, key);
    }

    /**
     * 刷新资源
     *
     * @param
     * @return void
     */
    public void refresh() {
        publisher.publishEvent(new RefreshGroupResourceEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public GroupConfigDao getGroupConfigDao() {
        return groupConfigDao;
    }
}