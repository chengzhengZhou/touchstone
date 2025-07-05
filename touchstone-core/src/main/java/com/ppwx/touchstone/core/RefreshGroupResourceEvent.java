/**
 * Copyright (C), 2010-2023, 爱回收
 * FileName: RefreshGroupResourceEvent
 * Author:   Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * Date:     2023/11/1 16:15
 * Description:
 */
package com.ppwx.touchstone.core;

import org.springframework.context.ApplicationEvent;

/**
 * 实验组资源更新事件
 *
 * @author Mikey(ext.ahs.zhouchzh1 @ jd.com)
 * @date 2023/11/01 16:15
 * @since 1.0.0
 */
public class RefreshGroupResourceEvent extends ApplicationEvent {

    /**
     * 创建一个更新事件
     *
     * @param source
     * @return
     */
    public RefreshGroupResourceEvent(Object source) {
        super(source);
    }
}