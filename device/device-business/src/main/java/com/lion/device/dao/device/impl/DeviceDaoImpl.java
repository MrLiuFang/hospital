package com.lion.device.dao.device.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.dao.device.DeviceDaoEx;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 下午2:29
 */
public class DeviceDaoImpl implements DeviceDaoEx {

    @Autowired
    private BaseDao<Device> baseDao;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Override
    public Page deviceMonitorList(Long buildId, Long buildFloorId, DeviceClassify deviceClassify, DeviceType deviceType, String state, String name, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<>();
        sb.append(" select d from Device d where 1=1 ");
        if (StringUtils.hasText(name)) {
            sb.append(" and d.name like :name ");
            searchParameter.put("name","%"+name+"%");
        }
        if (Objects.nonNull(buildId)){
            sb.append(" and d.buildId =:buildId ");
            searchParameter.put("buildId",buildId);
        }
        if (Objects.nonNull(buildFloorId)){
            sb.append(" and d.buildFloorId =:buildFloorId ");
            searchParameter.put("buildFloorId",buildFloorId);
        }
        List<State> states = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now().minusHours(24);
        if (Objects.equals(state,"1")) {
//            states.add(State.REPAIR);
//            states.add(State.FAULT);
//            states.add(State.OFF_LINE);
//            sb.append(" and d.deviceState not in :states ");
//            searchParameter.put("states", states);
            sb.append(" and ( d.lastDataTime is not null and d.lastDataTime > :dateTime )");
            searchParameter.put("dateTime", dateTime);
        }else if (Objects.equals(state,"2")) {
//            sb.append(" and d.deviceState = :deviceState ");
//            searchParameter.put("deviceState", State.OFF_LINE);
            sb.append(" and ( d.lastDataTime is null or d.lastDataTime < :dateTime )");
            searchParameter.put("dateTime", dateTime);
        }else if (Objects.equals(state,"3")) {
            sb.append(" and d.isFault is true ");
        }
        if (Objects.nonNull(deviceClassify)) {
            sb.append(" and d.deviceClassify =:deviceClassify ");
            searchParameter.put("deviceClassify", deviceClassify);
        }
        if (Objects.nonNull(deviceType)) {
            sb.append(" and d.deviceType =:deviceType ");
            searchParameter.put("deviceType", deviceType);
        }
        sb.append(" order by d.createDateTime ");
        return baseDao.findNavigator(lionPage,sb.toString(),searchParameter);
    }

    @Override
    public Page deviceState(LionPage lionPage) {
        Map<String, Object> searchParameter = new HashMap<>();
        List<Long> departmentIds = new ArrayList<>();
        departmentIds = departmentExposeService.responsibleDepartment(null);
        StringBuilder sb = new StringBuilder();
        sb.append(" select d from Device d where 1=1 and (battery = 2 or battery = 1) ");
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            List<Region> list = regionExposeService.findByDepartmentIds(departmentIds);
            List<Long> regionIds = new ArrayList<>();
            list.forEach(region -> {
                regionIds.add(region.getId());
            });
            if (Objects.nonNull(regionIds) && regionIds.size()>0){
                sb.append( " and regionId in :regionIds " );
                searchParameter.put("regionIds", regionIds);
            }
        }
//        sb.append( " and deviceState in :deviceState " );
//        List<State> list = new ArrayList<>();
//        list.add(State.USED);
//        list.add(State.ALARM);
//        searchParameter.put("deviceState", list);
//        sb.append(" order by d.deviceState desc ,battery desc ");
        return baseDao.findNavigator(lionPage,sb.toString(), searchParameter);
    }

    @Override
    public List<Device> find(List<Long> regionIds, String name, String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select d from Device d where 1=1 ");
        Map<String, Object> searchParameter = new HashMap<>();
        if (Objects.nonNull(regionIds) && regionIds.size()>0) {
            sb.append(" and d.regionId in :regionIds ");
            searchParameter.put("regionIds", regionIds);
        }
        if (StringUtils.hasText(name) || StringUtils.hasText(code)) {
            sb.append(" and ( ");
            boolean b = false;
            if (StringUtils.hasText(name)) {
                sb.append((Objects.equals(b,true)?" or ":"") + " d.name like :name ");
                searchParameter.put("name", "%"+name+"%");
                b = true;
            }
            if (StringUtils.hasText(code)) {
                sb.append((Objects.equals(b,true)?" or ":"") + " d.code like :code ");
                searchParameter.put("code", "%"+code+"%");
                b = true;
            }
            sb.append(" ) ");
        }
        return (List<Device>) baseDao.findAll(sb.toString(),searchParameter);
    }
}
