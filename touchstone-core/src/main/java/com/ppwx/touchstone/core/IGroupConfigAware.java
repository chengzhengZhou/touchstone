/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: IGroupConfigAware
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/26 15:41
 * Description: 项目一般同时存在多个实验计划
 */
package com.ppwx.touchstone.core;

/**
 *
 * 项目一般同时存在多个实验计划
 * 定义该接口可便于{@link ISelector}容器能感知到分组配置的变更
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 15:41
 * @since 1.0.0
 */
public interface IGroupConfigAware {

    /**
     * 实现该方法并用于配置更新时更新对应的算法参数
     *
     * @param groupConfig
     * @return void
     */
    void initWithConfig(IGroupTestConfig groupConfig);

}