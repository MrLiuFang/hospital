package com.lion.device.dao.device.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.dao.device.DeviceDaoEx;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 下午2:29
 */
public class DeviceDaoImpl implements DeviceDaoEx {

    @Autowired
    private BaseDao<Device> baseDao;

    @Override
    public Page deviceMonitorList(Long buildId, Long buildFloorId, State deviceState, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<>();
        sb.append(" select d from Device d where 1=1 ");
        if (Objects.nonNull(buildId)){
            sb.append(" and d.buildId =:buildId ");
            searchParameter.put("buildId",buildId);
        }
        if (Objects.nonNull(buildFloorId)){
            sb.append(" and d.buildFloorId =:buildFloorId ");
            searchParameter.put("buildFloorId",buildFloorId);
        }if (Objects.nonNull(deviceState)){
            sb.append(" and d.deviceState =:deviceState ");
            searchParameter.put("deviceState", deviceState);
        }
        sb.append(" order by d.createDateTime ");
        return baseDao.findNavigator(lionPage,sb.toString(),searchParameter);
    }
}
