package com.lion.device.expose.impl.device;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceGroupDao;
import com.lion.device.dao.device.DeviceGroupDeviceDao;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.device.DeviceGroupExposeService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:27
 */
@DubboService(interfaceClass = DeviceGroupExposeService.class )
public class DeviceGroupExposeServiceImpl extends BaseServiceImpl<DeviceGroup> implements DeviceGroupExposeService {

    @Autowired
    private DeviceGroupDao deviceGroupDao;

    @Autowired
    private DeviceGroupDeviceDao deviceGroupDeviceDao;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Override
    public int count(Long departmentId) {
//        List<Region> list = regionExposeService.findByDepartmentId(departmentId);
//        int count = list.stream().filter(region -> Objects.nonNull(region.getDeviceGroupId())).mapToInt(region -> deviceGroupDeviceDao.countByDeviceGroupId(region.getDeviceGroupId())).sum();
        return 0;
    }

    @Override
    public int count(Long departmentId, State state) {
//        List<Region> list = regionExposeService.findByDepartmentId(departmentId);
//        int count = list.stream().filter(region -> Objects.nonNull(region.getDeviceGroupId())).mapToInt(region -> deviceGroupDeviceDao.count(region.getDeviceGroupId(),state)).sum();
        return 0;
    }
}
