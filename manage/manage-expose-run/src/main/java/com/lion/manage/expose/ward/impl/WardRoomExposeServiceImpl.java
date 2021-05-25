package com.lion.manage.expose.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.expose.ward.WardRoomExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午1:56
 */
@DubboService(interfaceClass = WardRoomExposeService.class)
public class WardRoomExposeServiceImpl extends BaseServiceImpl<WardRoom> implements WardRoomExposeService {
}
