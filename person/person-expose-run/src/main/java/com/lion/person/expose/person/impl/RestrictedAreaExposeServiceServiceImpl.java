package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.expose.person.RestrictedAreaExposeServiceService;
import com.lion.person.service.person.RestrictedAreaService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 上午9:39
 */
@DubboService
public class RestrictedAreaExposeServiceServiceImpl extends BaseServiceImpl<RestrictedArea> implements RestrictedAreaExposeServiceService {

    @Autowired
    private RestrictedAreaService restrictedAreaService;

    @Override
    public List<RestrictedArea> find(Long id, PersonType personType) {
        return restrictedAreaService.find(id,personType);
    }
}
