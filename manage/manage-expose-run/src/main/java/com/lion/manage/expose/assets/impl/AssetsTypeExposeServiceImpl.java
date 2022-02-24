package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.expose.assets.AssetsTypeExposeService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(interfaceClass = AssetsTypeExposeService.class)
public class AssetsTypeExposeServiceImpl extends BaseServiceImpl<AssetsType> implements AssetsTypeExposeService {
}
