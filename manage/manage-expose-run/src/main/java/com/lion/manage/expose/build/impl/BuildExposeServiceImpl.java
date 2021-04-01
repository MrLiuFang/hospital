package com.lion.manage.expose.build.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.build.Build;
import com.lion.manage.expose.build.BuildExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:40
 */
@DubboService(interfaceClass = BuildExposeService.class)
public class BuildExposeServiceImpl extends BaseServiceImpl<Build> implements BuildExposeService {
}
