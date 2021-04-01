package com.lion.manage.expose.build.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.expose.build.BuildFloorExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:41
 */
@DubboService(interfaceClass = BuildFloorExposeService.class)
public class BuildFloorExposeServiceImpl extends BaseServiceImpl<BuildFloor> implements BuildFloorExposeService {
}
