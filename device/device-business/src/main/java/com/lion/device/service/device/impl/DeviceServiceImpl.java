package com.lion.device.service.device.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.vo.DeviceStatisticsVo;
import com.lion.device.entity.device.vo.ListDeviceMonitorVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.device.DeviceGroupExposeService;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceService;
import com.lion.device.entity.device.Device;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:46
 */
@Service
public class DeviceServiceImpl extends BaseServiceImpl<Device> implements DeviceService {

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private CctvDao cctvDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private DeviceGroupDeviceService deviceGroupDeviceService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @DubboReference
    private DeviceGroupExposeService deviceGroupExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Override
    public void update(Device entity) {
        entity = setWarrantyPeriodDate(entity);
        assertNameExist(entity.getName(),entity.getId());
        assertCodeExist(entity.getCode(),entity.getId());
        super.update(entity);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE+entity.getId(),entity, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+entity.getCode(),entity, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public <S extends Device> S save(S entity) {
        entity = (S) setWarrantyPeriodDate(entity);
        assertNameExist(entity.getName(),null);
        assertCodeExist(entity.getCode(),null);
        entity = super.save(entity);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE+entity.getId(),entity, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+entity.getCode(),entity, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        return entity;
    }

    private void assertNameExist(String name, Long id) {
        Device device = deviceDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(device)) || (Objects.nonNull(id) && Objects.nonNull(device) && !Objects.equals(device.getId(),id )) ){
            BusinessException.throwException("该设备名称已存在");
        }
    }

    private void assertCodeExist(String code, Long id) {
        Device device = deviceDao.findFirstByCode(code);
        if ((Objects.isNull(id) && Objects.nonNull(device)) || ( Objects.nonNull(id) && Objects.nonNull(device) && !Objects.equals(device.getId(),id)) ){
            BusinessException.throwException("该设备编号已存在");
        }
    }

    private Device setWarrantyPeriodDate(Device device){
        if (Objects.nonNull(device.getPurchaseDate()) && Objects.nonNull(device.getWarrantyPeriod())){
            device.setWarrantyPeriodDate(device.getPurchaseDate().plusMonths(Long.valueOf(device.getWarrantyPeriod())));
        }
        return device;
    }


    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            Device device = this.findById(d.getId());
            if (Objects.nonNull(device) ) {
                deleteById(d.getId());
                deviceGroupDeviceService.deleteByDeviceId(d.getId());
                redisTemplate.delete(RedisConstants.DEVICE+device.getId());
                redisTemplate.delete(RedisConstants.DEVICE_CODE+device.getCode());
                redisTemplate.delete(RedisConstants.DEVICE_REGION+device.getId());
            }
        });
    }

    @Override
    public DeviceStatisticsVo statistics() {
        DeviceStatisticsVo ov = new DeviceStatisticsVo();
        List<DeviceStatisticsVo.DeviceStatisticsData> list = new ArrayList<>();
        list.add(count(DeviceClassify.HAND_WASHING));
        list.add(count(DeviceClassify.LF_EXCITER));
        list.add(count(DeviceClassify.MONITOR));
        list.add(count(DeviceClassify.RECYCLING_BOX));
        list.add(count(DeviceClassify.STAR_AP));
        list.add(count(DeviceClassify.VIRTUAL_WALL));
        DeviceStatisticsVo.DeviceStatisticsData dataCctv = new DeviceStatisticsVo.DeviceStatisticsData();
        dataCctv.setName("cctv");
        dataCctv.setCode("CCTV");
        dataCctv.setCount(cctvDao.count());
        list.add(dataCctv);
//        DeviceStatisticsVo.DeviceStatisticsData dataTag = new DeviceStatisticsVo.DeviceStatisticsData();
//        dataTag.setName("tag");
//        dataTag.setName("TAG");
//        dataTag.setCount(tagDao.count());
//        list.add(dataTag);
        ov.setList(list);
        return ov;
    }

    @Override
    public List<Long> allId() {
        return deviceDao.allId();
    }

    @Override
    public IPageResultData<List<ListDeviceMonitorVo>> deviceMonitorList(Long buildId, Long buildFloorId, State deviceState, LionPage lionPage) {
        Page<Device> page = deviceDao.deviceMonitorList(buildId, buildFloorId, deviceState, lionPage);
        List<ListDeviceMonitorVo> returnList = new ArrayList<>();
        List<Device> list = page.getContent();
        list.forEach(device -> {
            ListDeviceMonitorVo vo = new ListDeviceMonitorVo();
            vo.setBattery(device.getBattery());
            Build build = buildExposeService.findById(device.getBuildId());
            vo.setBuildName(Objects.isNull(build)?"":build.getName());
            BuildFloor buildFloor = buildFloorExposeService.findById(device.getBuildFloorId());
            vo.setBuildFloorName(Objects.isNull(buildFloor)?"":buildFloor.getName());
            vo.setClassify(device.getDeviceClassify());
            vo.setCode(device.getCode());
            vo.setName(device.getName());
            DeviceGroupDevice deviceGroupDevice = deviceGroupDeviceExposeService.findByDeviceId(device.getId());
            if (Objects.nonNull(deviceGroupDevice)) {
                DeviceGroup deviceGroup = deviceGroupExposeService.findById(deviceGroupDevice.getDeviceGroupId());
                Region region = regionExposeService.find(deviceGroup.getId());
                if (Objects.nonNull(region)) {
                    Department department = departmentExposeService.findById(region.departmentId);
                    if (Objects.nonNull(department)) {
                        vo.setDepartmentName(department.getName());
                    }
                }
            }
            vo.setImg(device.getImg());
            vo.setImgUrl(fileExposeService.getUrl(device.getImg()));
            vo.setState(device.getDeviceState());
            if (Objects.nonNull(device.getLastDataTime())) {
                Duration duration = Duration.between(device.getLastDataTime(), LocalDateTime.now());
                vo.setIsOnline(duration.toMinutes()<120);
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    private DeviceStatisticsVo.DeviceStatisticsData count(DeviceClassify classify){
        DeviceStatisticsVo.DeviceStatisticsData data = new DeviceStatisticsVo.DeviceStatisticsData();
        data.setName(DeviceClassify.HAND_WASHING.getName());
        data.setCode(classify.getName());
        data.setCount(deviceDao.countByDeviceClassify(classify));
        return data;
    }
}
