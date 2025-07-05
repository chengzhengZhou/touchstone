/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: ISelector
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/10/26 13:58
 * Description:
 */
package com.ppwx.touchstone.core;

/**
 * 分桶分流操作接口
 * 一般定义一项分流计划时需要设置好对应的组及相关比例，后续请求时会根据特定key固定落到某个桶中
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/10/26 13:58
 * @since 1.0.0
 */
public interface ISelector {

    /**
     * 为当前key对象选择一个组
     *
     * @param key 用于决定所落的桶，尽量不要让key为空值
     * @return com.ppwx.touchstone.Group
     */
    Group select(Object key);

}