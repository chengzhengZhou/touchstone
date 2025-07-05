package com.ppwx.touchstone.core;

import java.util.List;

/**
 * 分组实验计划配置
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 15:49
 * @since 1.0.0
 */
public interface IGroupTestConfig {

    /**
     * 配置版本号
     */
    String VER_0 = "0";

    /**
     * 配置项分隔符
     */
    String GROUP_SEPARATOR = "|";

    /**
     * 分组命名空间
     *
     * @return java.lang.String
     */
    String namespace();

    /**
     * 分组实验名称
     *
     * @return java.lang.String
     */
    String groupTestName();

    /**
     * 获取实验计划下分组信息
     *
     * @return java.util.List<com.ppwx.touchstone.Group.MetaInfo>
     */
    List<Group> getGroupBeans();

    /**
     * 最新更新时间
     * 若更新时间大于数据时间则需要执行相关变更操作
     *
     * @return java.lang.Long
     */
    Long lastModify();

}
