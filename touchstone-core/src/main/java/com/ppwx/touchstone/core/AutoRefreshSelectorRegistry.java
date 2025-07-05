/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: AutoRefreshSelectorRegistry
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/31 14:04
 * Description: 支持动态更新实验组配置的注册器
 */
package com.ppwx.touchstone.core;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 支持动态更新实验组配置的注册器
 *
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/31 14:04
 * @since 1.0.0
 */
@Slf4j
public class AutoRefreshSelectorRegistry implements SelectorRegistry, InitializingBean, DisposableBean, ApplicationListener<RefreshGroupResourceEvent> {

    /**
     * 实验组分流筛选器
     */
    private final Map<String, ISelector> selectorMap = new ConcurrentHashMap<>(16);

    /**
     * 命名空间下需要更新的bean
     */
    private final Map<String, Map<String, IGroupConfigAware>> configAwareMap = new ConcurrentHashMap<>(8);

    /**
     * 命名空间下的版本号
     */
    private final Map<String, Long> namespaceVerMap = new ConcurrentHashMap<>(16);

    /**
     * 更新配置的频率
     */
    private long refreshInternalSeconds = 3;

    /**
     * 延迟线程池
     */
    private ScheduledThreadPoolExecutor executor;

    /**
     * 数据库配置源加载
     */
    private GroupResourceLoader groupResourceLoader;

    public void setGroupResourceLoader(GroupResourceLoader groupResourceLoader) {
        this.groupResourceLoader = groupResourceLoader;
    }

    @Override
    public void register(String namespace, String groupTestName, ISelector selector) {
        if (log.isDebugEnabled()) {
            log.debug("Register selector.namespace:{}, groupTestName:{}", namespace, groupTestName);
        }
        String name = namespace + "." + groupTestName;
        ISelector old = selectorMap.put(name, selector);
        if (old != null) {
            log.warn("duplicated group：{}", name);
        }
    }

    @Override
    public ISelector find(String namespace, String groupTestName) {
        return selectorMap.get(namespace + "." + groupTestName);
    }

    /**
     * 刷新配置
     *
     * @param
     * @return void
     */
    private void refresh() {
        Set<String> namespaces = groupResourceLoader.getNamespaces();
        namespaces.forEach(namespace -> {
            Long version = groupResourceLoader.getVersion(namespace);
            if (!Objects.equals(version, namespaceVerMap.get(namespace))) {
                List<IGroupTestConfig> configs = groupResourceLoader.loadAll(namespace);
                Map<String, IGroupConfigAware> configAwareMap = this.configAwareMap.getOrDefault(namespace, Collections.emptyMap());
                // reload
                configs.forEach(config -> {
                    if (configAwareMap.containsKey(config.groupTestName())) {
                        configAwareMap.get(config.groupTestName()).initWithConfig(config);
                    } else if(find(config.namespace(), config.groupTestName()) == null) {
                        // create selector and register
                        create(config);
                    }
                });
                configAwareMap.keySet().forEach(k -> {
                    if (configs.stream().noneMatch(config -> StrUtil.equals(config.groupTestName(), k))) {
                        configAwareMap.remove(k);
                        selectorMap.remove(namespace + "." + k);
                    }
                });

                // update version
                if (version == null) {
                    namespaceVerMap.remove(namespace);
                } else {
                    namespaceVerMap.put(namespace, version);
                }
            }
        });
    }

    public void create(IGroupTestConfig config) {
        BaseMemorySelector selector = new BaseMemorySelector();
        selector.initWithConfig(config);
        register(config.namespace(), config.groupTestName(), selector);

        // default version 0
        Map<String, IGroupConfigAware> configAwareMap = this.configAwareMap.computeIfAbsent(config.namespace(), k -> new ConcurrentHashMap<>(16));
        configAwareMap.put(config.groupTestName(), selector);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Touchstone.setSelectorRegistry(this);
        groupResourceLoader.loadAll().forEach(this::create);
        // begin task
        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("TouchstoneTask", true));
            executor.scheduleWithFixedDelay(this::refresh, 3, refreshInternalSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void setRefreshInternalSeconds(long refreshInternalSeconds) {
        this.refreshInternalSeconds = refreshInternalSeconds;
    }

    @Override
    public void onApplicationEvent(RefreshGroupResourceEvent refreshGroupResourceEvent) {
        refresh();
    }
}