package com.lion.upms.expose.user.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.expose.user.UserTypeExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 下午1:59
 */
@DubboService(interfaceClass = UserTypeExposeService.class)
public class UserTypeExposeServiceImpl extends BaseServiceImpl<UserType> implements UserTypeExposeService {
}
