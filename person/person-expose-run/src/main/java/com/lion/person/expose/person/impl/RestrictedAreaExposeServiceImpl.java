package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.RestrictedAreaDao;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.expose.person.RestrictedAreaExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 上午11:44
 */
@DubboService
public class RestrictedAreaExposeServiceImpl extends BaseServiceImpl<RestrictedArea> implements RestrictedAreaExposeService {

    @Autowired
    private RestrictedAreaDao restrictedAreaDao;

    @Override
    public List<RestrictedArea> find(Long personId) {
        return restrictedAreaDao.findByPersonId(personId);
    }
}
