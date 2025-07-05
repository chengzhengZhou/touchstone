package com.ppwx.touchstone.core;

import java.util.List;
import java.util.Set;

/**
 * 实验组资源加载器
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 23:47
 * @since 1.0.0
 */
public interface GroupResourceLoader {

    /**
     * 加载所有实验计划配置
     *
     * @param
     * @return java.util.List<com.ppwx.touchstone.IGroupTestConfig>
     */
    List<IGroupTestConfig> loadAll();

    /**
     * 获取配置的版本
     *
     * @param namespace 命名空间
     * @return java.lang.Long
     */
    Long getVersion(String namespace);

    /**
     * 获取命名空间下的所有实验配置
     *
     * @param namespace
     * @return java.util.List<com.ppwx.touchstone.IGroupTestConfig>
     */
    List<IGroupTestConfig> loadAll(String namespace);

    /**
     * 获取命名空间
     * @return java.util.Set
     */
    Set<String> getNamespaces();
}
