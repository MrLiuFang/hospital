package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        temporaryPersonDao.updateState(id, State.instance(state));
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        temporaryPersonDao.updateLastDataTime(id,dateTime);
    }

    @Override
    public int count(Long departmentId, State deviceSate) {
        if (Objects.isNull(deviceSate)){
            return temporaryPersonDao.countByDepartmentIdAndIsLeave(departmentId,false);
        }
        return temporaryPersonDao.countByDepartmentIdAndIsLeaveAndDeviceSate(departmentId,false,deviceSate);
    }

    @Override
    public List<TemporaryPerson> find(Long departmentId, String name) {
        if (StringUtils.hasText(name)){
            return temporaryPersonDao.findByDepartmentIdAndIsLeaveAndNameLike(departmentId,false,"%"+name+"%");
        }
        return temporaryPersonDao.findByDepartmentIdAndIsLeave(departmentId,false);
    }

    @Override
    public void updateIsWaitLeave(Long id, Boolean isWaitLeave) {
        temporaryPersonDao.updateIsWaitLeave(id,isWaitLeave);
    }
}
