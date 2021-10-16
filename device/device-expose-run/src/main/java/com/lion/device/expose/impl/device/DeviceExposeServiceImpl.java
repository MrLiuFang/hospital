package com.lion.device.expose.impl.device;

import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.service.device.DeviceService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:25
 */
@DubboService(interfaceClass = DeviceExposeService.class )
public class DeviceExposeServiceImpl extends BaseServiceImpl<Device> implements DeviceExposeService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Override
    public List<Device> findByDeviceGruopId(Long deviceGroupId) {
        return deviceDao.findByDeviceGroupId(deviceGroupId);
    }

    @Override
    public Device find(String code) {
        return deviceDao.findFirstByCode(code);
    }

    @Override
    public void updateBattery(Long deviceId, Integer battery) {
        Device device = deviceService.findById(deviceId);
        if (Objects.nonNull(device)) {
            device.setBattery(battery);
            update(device);
            redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+device.getCode(),device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.DEVICE+device.getId(),device, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
    }

    @Override
    public Integer countDevice(List<Long> deviceGroupIds, Integer battery) {
        return deviceDao.countDevice(deviceGroupIds, battery);
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        deviceDao.updateLastDataTime(id,dateTime);
    }

    @Override
    public void updateState(Long id, Integer state) {
        deviceDao.updateState(id, State.instance(state));
    }

    @Override
    public List<Device> find(LocalDateTime startPreviousDisinfectDate, LocalDateTime endPreviousDisinfectDate, String name, String code) {
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        if (Objects.nonNull(startPreviousDisinfectDate)) {
            searchParameter.put(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_previousDisinfectDate",startPreviousDisinfectDate);
        }
        if (Objects.nonNull(endPreviousDisinfectDate)) {
            searchParameter.put(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_previousDisinfectDate",endPreviousDisinfectDate);
        }
        if (StringUtils.hasText(name)) {
            searchParameter.put(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)) {
            searchParameter.put(SearchConstant.LIKE+"_code",code);
        }
        searchParameter.put(SearchConstant.EQUAL+"_deviceClassify", DeviceClassify.RECYCLING_BOX);
        return this.find(searchParameter);
    }

    @Override
    public void updateDisinfectDate(Long id) {
        this.deviceDao.updateDisinfectDate(id, LocalDate.now());
    }

    @Override
    public List<Long> allId() {
        return deviceDao.allId();
    }

    @Override
    public List<Device> findByRegionId(Long regionId) {
        return deviceDao.findByRegionId(regionId);
    }

    @Override
    public List<Device> findByDepartmentId(Long departmentId) {
        List<Region> regionList = regionExposeService.findByDepartmentId(departmentId);
        List<Long> reginIds = new ArrayList<>();
        reginIds.add(Long.MAX_VALUE);
        regionList.forEach(region -> {
            reginIds.add(region.getId());
        });
        return deviceDao.findByRegionIdIn(reginIds);
    }

    @Override
    @Transactional
    public void relationRegion(Long regionId, List<Long> ids) {
        if (Objects.isNull(ids) ){
            ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
        }
        ids.forEach(deviceId ->{
            Device device = deviceDao.findFirstByIdAndRegionIdNotNull(deviceId);
            if (Objects.nonNull(device)) {
                BusinessException.throwException(MessageI18nUtil.getMessage("2000119",new Object[]{device.getName()}));
            }
        });
        deviceDao.updateRegionIdIsNull(regionId);
        deviceDao.updateRegion(regionId, ids);
    }

    @Override
    public int count(DeviceClassify classify, Long regionId) {
        return deviceDao.countByDeviceClassifyAndRegionId(classify,regionId);
    }

    @Override
    public int count(Long departmentId, List<State> states) {
        List<Region> regionList = regionExposeService.findByDepartmentId(departmentId);
        List<Long> reginIds = new ArrayList<>();
        reginIds.add(Long.MAX_VALUE);
        regionList.forEach(region -> {
            reginIds.add(region.getId());
        });
        return deviceDao.countByRegionIdInAndDeviceStateIn(reginIds,states);
    }

    @Override
    public DetailsDeviceVo details(Long id) {
        return deviceService.details(id);
    }

}
