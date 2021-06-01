package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午2:39
 */
@DubboService(interfaceClass = TemporaryPersonExposeService.class)
public class TemporaryPersonExposeServiceImpl extends BaseServiceImpl<TemporaryPerson> implements TemporaryPersonExposeService {
}
