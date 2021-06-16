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
 * @Description //TODO
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
    public Integer countByDepartmentId(Long departmentId, State deviceState) {
        if (Objects.isNull(deviceState)) {
            return assetsDao.countByDepartmentId(departmentId);
        }
        return assetsDao.countByDepartmentIdAndDeviceState(departmentId,deviceState);
    }

    @Override
    public List<Assets> findByDepartmentId(Long departmentId) {
        return assetsDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Assets> findByDepartmentId(Long departmentId, String name, String code) {
        if (StringUtils.hasText(name) && StringUtils.hasText(code)) {
            return assetsDao.findByDepartmentIdOrNameLikeOrCodeLike(departmentId, "%"+name+"%", "%"+code+"%");
        }
        return assetsDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Long> allId() {
        return assetsDao.allId();
    }

    @Override
    public void updateState(Long id, Integer state) {
        assetsDao.updateState(id, State.instance(state));
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        assetsDao.updateLastDataTime(id,dateTime);
    }


}
