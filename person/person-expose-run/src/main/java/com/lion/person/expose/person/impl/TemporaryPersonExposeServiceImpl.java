package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/1 下午2:39
 */
@DubboService(interfaceClass = TemporaryPersonExposeService.class)
public class TemporaryPersonExposeServiceImpl extends BaseServiceImpl<TemporaryPerson> implements TemporaryPersonExposeService {

    @Autowired
    private TemporaryPersonDao temporaryPersonDao;

    @Override
    public void updateState(Long id, Integer state) {
        temporaryPersonDao.update(id,state);
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        temporaryPersonDao.update(id,dateTime);
    }
}
