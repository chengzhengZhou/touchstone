/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: GroupConfig
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/26 16:10
 * Description: 分组实验计划配置实现
 */
package com.ppwx.touchstone.core.domain;

import cn.hutool.core.util.StrUtil;
import com.ppwx.touchstone.core.Group;
import com.ppwx.touchstone.core.IGroupTestConfig;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.beans.Transient;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * 分组实验计划配置实现
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 16:10
 * @since 1.0.0
 */
@Data
@Validated
public class GroupConfig implements IGroupTestConfig {

    /**
     * 主键
     */
    private Long id;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 分组实验计划名称
     */
    private String testName;

    /**
     * 分组实验计划下的实验组信息
     */
    private String testGroups;

    /**
     * 白名单
     */
    private String whitelist;

    /**
     * 随机数
     */
    private Integer rndSeed;

    /**
     * 更新时间
     */
    private Date updateDt;

    /**
     * 分组实验计划下的实验组实体
     */
    private transient List<Group> groupBeans;

    @Override
    public String namespace() {
        return this.namespace;
    }

    @Override
    public String groupTestName() {
        return this.testName;
    }

    @Override
    public List<Group> getGroupBeans() {
        return this.groupBeans;
    }

    @Override
    public Long lastModify() {
        return this.updateDt == null ? null : this.updateDt.getTime();
    }

    /**
     * 对{@link Group}进行规则化
     * groups -> testGroups
     *
     * @param
     * @return com.ppwx.touchstone.domain.GroupConfig
     */
    @Transient
    public final GroupConfig normalizeGroup() {
        if (this.groupBeans != null) {
            validGroup();
            /*
             * 0|groupNo|rate|idx1,idx..;
             * 版本、分组编号、比例、分桶下标
             */
            this.testGroups = groupBeans.stream().map(item -> VER_0 + GROUP_SEPARATOR +
                    item.getMetaInfo().getGroupNo() + GROUP_SEPARATOR +
                    item.getMetaInfo().getRate() + GROUP_SEPARATOR +
                    item.getAssignedBuckets().stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .collect(Collectors.joining(";"));
            /*
             * groupNo|white1,white..;
             */
            this.whitelist = groupBeans.stream()
                    .filter(item -> !CollectionUtils.isEmpty(item.getWhitelist()))
                    .map(item -> {
                        String groupNo = item.getMetaInfo().getGroupNo();
                        return groupNo + GROUP_SEPARATOR + String.join(",", item.getWhitelist());
                    }).collect(Collectors.joining(";"));

        }
        return this;
    }

    /**
     * 解析testGroups到{@link Group}s
     *
     * @param
     * @return com.ppwx.touchstone.domain.GroupConfig
     */
    @Transient
    public final GroupConfig parseGroupConfig() {
        if (this.testGroups != null) {
            List<Group> groups = new ArrayList<>();
            List<String> lines = StrUtil.split(this.testGroups, ";");
            for (String line : lines) {
                Iterator<String> propIterator = StrUtil.split(line, GROUP_SEPARATOR).iterator();
                String version = propIterator.next();
                if (!Objects.equals(version, VER_0)) {
                    continue;
                }
                String groupNo = propIterator.next();
                int rate = Integer.parseInt(propIterator.next());
                String bucketIdxStr = propIterator.next();
                if (StringUtils.isEmpty(bucketIdxStr)) {
                    groups.add(new Group(groupNo, rate));
                } else {
                    List<Integer> idxes = StrUtil.split(bucketIdxStr, ",")
                            .stream().map(Integer::valueOf).collect(Collectors.toList());
                    groups.add(new Group(groupNo, rate, idxes));
                }
            }
            // add white list
            if (StrUtil.isNotBlank(this.whitelist)) {
                lines = StrUtil.split(this.whitelist, ";");
                for (String line : lines) {
                    Iterator<String> whiteIterator = StrUtil.split(line, GROUP_SEPARATOR).iterator();
                    String groupNo = whiteIterator.next();
                    // how about mismatch group?
                    groups.stream()
                            .filter(item -> StrUtil.equals(groupNo, item.getMetaInfo().getGroupNo())).findFirst()
                            .ifPresent(item -> item.setWhitelist(StrUtil.split(whiteIterator.next(), ",")));

                }
            }

            this.groupBeans = groups;
            validGroup();
        }
        return this;
    }

    /**
     * 校验实验组配置合规性
     *
     * @param
     * @return void
     */
    private void validGroup() {
        if (CollectionUtils.isEmpty(this.getGroupBeans())) {
            throw new IllegalArgumentException("group config is empty");
        }
        int minRate = 0;
        int maxRate = 100;
        Set<String> groupNoSet = new HashSet<>();
        AtomicInteger totalRate = new AtomicInteger();
        this.groupBeans.forEach(item -> {
            Group.MetaInfo metaInfo = item.getMetaInfo();
            if (groupNoSet.contains(metaInfo.getGroupNo())) {
                throw new IllegalArgumentException("冲突的分组编号" + metaInfo.getGroupNo());
            }
            if (metaInfo.getRate() < minRate || metaInfo.getRate() > maxRate) {
                throw new IllegalArgumentException("rate比率必须在[0,100]范围");
            }
            if (item.getAssignedBuckets().size() != metaInfo.getRate()) {
                throw new IllegalArgumentException("rate比率和实际分桶数不匹配");
            }
            groupNoSet.add(metaInfo.getGroupNo());
            totalRate.addAndGet(metaInfo.getRate());
        });

        if (totalRate.get() > maxRate) {
            throw new IllegalArgumentException("累计rate溢出，当前值：" + totalRate.get());
        }
    }

}