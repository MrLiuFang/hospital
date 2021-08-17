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
    public int count(Long departmentId, State deviceState, List<Long> ids) {
        if (Objects.isNull(deviceState) && (Objects.isNull(ids) || ids.size()<=0)){
            return temporaryPersonDao.countByDepartmentIdAndIsLeave(departmentId,false);
        }else if (Objects.isNull(deviceState) && (Objects.nonNull(ids) || ids.size()>0)){
            return temporaryPersonDao.countByDepartmentIdAndIsLeaveAndIdIn(departmentId,false, ids);
        }

        if (Objects.nonNull(deviceState) && (Objects.isNull(ids) || ids.size()<=0)){
            return temporaryPersonDao.countByDepartmentIdAndIsLeaveAndDeviceState(departmentId,false, deviceState);
        }else if (Objects.nonNull(deviceState) && (Objects.nonNull(ids) || ids.size()>0)){
            return temporaryPersonDao.countByDepartmentIdAndIsLeaveAndDeviceStateAndIdIn(departmentId,false, deviceState,ids);
        }
        return temporaryPersonDao.countByDepartmentIdAndIsLeaveAndDeviceState(departmentId,false, deviceState);
    }

    @Override
    public List<TemporaryPerson> find(Long departmentId, String name, List<Long> ids) {
        if (StringUtils.hasText(name) && (Objects.isNull(ids) || ids.size() <=0)){
            return temporaryPersonDao.findByDepartmentIdAndIsLeaveAndNameLike(departmentId,false,"%"+name+"%");
        }else if (StringUtils.hasText(name) && (Objects.nonNull(ids) || ids.size() >0)){
            return temporaryPersonDao.findByDepartmentIdAndIsLeaveAndNameLikeAndIdIn(departmentId,false,"%"+name+"%",ids);
        }

        if (!StringUtils.hasText(name) && (Objects.isNull(ids) || ids.size() <=0)){
            return temporaryPersonDao.findByDepartmentIdAndIsLeave(departmentId, false);
        }else if (!StringUtils.hasText(name) && (Objects.nonNull(ids) || ids.size() >0)){
            return temporaryPersonDao.findByDepartmentIdAndIsLeaveAndIdIn(departmentId, false,ids);
        }

        return temporaryPersonDao.findByDepartmentIdAndIsLeave(departmentId, false);
    }

    @Override
    public void updateIsWaitLeave(Long id, Boolean isWaitLeave) {
        temporaryPersonDao.updateIsWaitLeave(id,isWaitLeave);
    }
}
