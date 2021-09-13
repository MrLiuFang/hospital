package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.expose.rule.WashTemplateExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/13 下午4:30
 */
@DubboService(interfaceClass = WashTemplateExposeService.class)
public class WashTemplateExposeServiceImpl extends BaseServiceImpl<WashTemplate> implements WashTemplateExposeService {
}
