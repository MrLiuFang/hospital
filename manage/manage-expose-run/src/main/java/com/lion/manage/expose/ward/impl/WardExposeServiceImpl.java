package com.lion.manage.expose.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.expose.ward.WardExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午1:58
 */
@DubboService(interfaceClass = WardExposeService.class)
public class WardExposeServiceImpl extends BaseServiceImpl<Ward> implements WardExposeService {
}
