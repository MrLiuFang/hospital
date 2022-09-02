package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.State;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.service.assets.AssetsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/16 上午9:50
 **/
@DubboService
public class AssetsExposeServiceImpl extends BaseServiceImpl<Assets> implements AssetsExposeService {

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private AssetsDao assetsDao;

    @Override
    public Assets find(Long tagId) {
        return assetsService.findByTagId(tagId);
    }

    @Override
    public Assets find(String code) {
        return assetsDao.findFirstByCode(code);
    }

    @Override
    public List<Map<String, Object>> count(Long buildFloorId) {
        return assetsDao.groupRegionCount(buildFloorId);
    }

    @Override
    public Integer countByDepartmentId(Long departmentId, State deviceState, List<Long> assetsIds) {
        return assetsDao.count(departmentId, deviceState,assetsIds);
    }

    @Override
    public Integer count(Long departmentId, State deviceState, Boolean isAlarm, Boolean isFault, List<Long> assetsIds) {
        if (Objects.nonNull(assetsIds) && assetsIds.size()>0) {
            return assetsDao.countByDepartmentIdAndDeviceStateAndIsAlarmAndIsFaultAndIdIn(departmentId,deviceState,isAlarm,isFault,assetsIds);
        }else {
            return assetsDao.countByDepartmentIdAndDeviceStateAndIsAlarmAndIsFault(departmentId,deviceState,isAlarm,isFault);
        }
    }

    @Override
    public List<Assets> findByDepartmentId(Long departmentId) {
        return assetsDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Assets> findByDepartmentId(Long departmentId, String name, String code, List<Long> ids) {
        if (StringUtils.hasText(name) && StringUtils.hasText(code) && (Objects.isNull(ids) || ids.size()<=0)) {
            return assetsDao.findByDepartmentIdOrNameLikeOrCodeLike(departmentId, "%"+name+"%", "%"+code+"%");
        }else if (StringUtils.hasText(name) && StringUtils.hasText(code) && (Objects.nonNull(ids) && ids.size()>0)) {
            return assetsDao.findByDepartmentIdOrNameLikeOrCodeLikeAndIdIn(departmentId, "%"+name+"%", "%"+code+"%",ids);
        }else if (!StringUtils.hasText(name) && !StringUtils.hasText(code) && (Objects.nonNull(ids) && ids.size()>0)) {
            return assetsDao.findByDepartmentIdAndIdIn(departmentId, ids);
        }else if (!StringUtils.hasText(name) && !StringUtils.hasText(code) && (Objects.isNull(ids) || ids.size()<=0)) {
            return assetsDao.findByDepartmentId(departmentId);
        }
        return assetsDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Long> allId() {
        return assetsDao.allId();
    }

    @Override
    public void updateState(Long id, Integer state) {
        assetsDao.updateState(id, Objects.equals(state,1)?false:true);
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        assetsDao.updateLastDataTime(id,dateTime);
    }

    @Override
    public Integer countFault(Long departmentId) {

        return assetsDao.countByDepartmentIdAndIsFaultIsTrue(departmentId);
    }


}
