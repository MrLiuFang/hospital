package com.lion.manage.expose.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.region.RegionExposeObject;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午2:59
 **/
@DubboService(interfaceClass = RegionExposeObjectExposeService.class)
public class RegionExposeObjectExposeServiceImpl extends BaseServiceImpl<RegionExposeObject> implements RegionExposeObjectExposeService {
}
