package com.lion.device.service.device.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.device.dto.ReplaceDeviceDto;
import com.lion.device.entity.device.vo.DetailsDeviceVo;
import com.lion.device.entity.device.vo.DeviceStatisticsVo;
import com.lion.device.entity.device.vo.ListDeviceMonitorVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.device.DeviceGroupExposeService;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceService;
import com.lion.device.entity.device.Device;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @Override
    public void update(Device entity) {
        entity = setWarrantyPeriodDate(entity);
        assertNameExist(entity.getName(),entity.getId());
        assertCodeExist(entity.getCode(),entity.getId());
        super.update(entity);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE+entity.getId(),entity, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+entity.getCode(),entity, 5, TimeUnit.MINUTES);
    }

    @Override
    public <S extends Device> S save(S entity) {
        entity = (S) setWarrantyPeriodDate(entity);
        assertNameExist(entity.getName(),null);
        assertCodeExist(entity.getCode(),null);
        entity = super.save(entity);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE+entity.getId(),entity, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.DEVICE_CODE+entity.getCode(),entity, 5, TimeUnit.MINUTES);
        return entity;
    }

    private void assertNameExist(String name, Long id) {
        Device device = deviceDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(device)) || (Objects.nonNull(id) && Objects.nonNull(device) && !Objects.equals(device.getId(),id )) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000038"));
        }
    }

    private void assertCodeExist(String code, Long id) {
        Device device = deviceDao.findFirstByCode(code);
        if ((Objects.isNull(id) && Objects.nonNull(device)) || ( Objects.nonNull(id) && Objects.nonNull(device) && !Objects.equals(device.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000039"));
        }
    }

    private Device setWarrantyPeriodDate(Device device){
        if (Objects.nonNull(device.getPurchaseDate()) && Objects.nonNull(device.getWarrantyPeriod())){
            device.setWarrantyPeriodDate(device.getPurchaseDate().plusMonths(Long.valueOf(device.getWarrantyPeriod())));
        }
        return device;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Device> delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            Optional<Device> optional = findById(deleteDto.getId());
            optional.ifPresent(device -> {
                if (Objects.equals(device.getDeviceState(),State.USED)) {
                    BusinessException.throwException("設備使用中不能刪除");
                }
            });
        });
        List<Device> list = new ArrayList<>();
        deleteDtoList.forEach(d->{
            com.lion.core.Optional<Device> optional = this.findById(d.getId());
            if (optional.isPresent() ) {
                Device device = optional.get();
                del(d.getId());
//                deviceGroupDeviceService.deleteByDeviceId(d.getId());
                redisTemplate.delete(RedisConstants.DEVICE+device.getId());
                redisTemplate.delete(RedisConstants.DEVICE_CODE+device.getCode());
                redisTemplate.delete(RedisConstants.DEVICE_REGION+device.getId());
                currentPositionExposeService.delete(null,d.getId(),null);
                Device newDevice = new Device();
                newDevice.setDeviceState(State.NOT_USED);
                newDevice.setCode(device.getCode());
                newDevice.setDeviceType(device.getDeviceType());
                newDevice.setDeviceClassify(device.getDeviceClassify());
//                save(newDevice);
                list.add(newDevice);
            }
        });
        return list;
//        saveNewDevice(list);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void del(Long id) {
        this.deleteById(id);
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void saveNewDevice(List<Device> list) {
//        list.forEach(device -> {
//            save(device);
//        });
//    }

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
//        DeviceStatisticsVo.DeviceStatisticsData dataCctv = new DeviceStatisticsVo.DeviceStatisticsData();
//        dataCctv.setName("cctv");
//        dataCctv.setCode("CCTV");
//        dataCctv.setCount(cctvDao.count());
//        list.add(dataCctv);
//        DeviceStatisticsVo.DeviceStatisticsData dataTag = new DeviceStatisticsVo.DeviceStatisticsData();
//        dataTag.setName("tag");
//        dataTag.setName("TAG");
//        dataTag.setCount(tagDao.count());
//        list.add(dataTag);
        ov.setList(list);
        return ov;
    }

    @Override
    public Page<List<Device>> deviceState(LionPage lionPage) {
        return deviceDao.deviceState(lionPage);
    }

    @Override
    public List<Long> allId() {
        return deviceDao.allId();
    }

    @Override
    public IPageResultData<List<ListDeviceMonitorVo>> deviceMonitorList(Long buildId, Long buildFloorId, DeviceClassify deviceClassify, DeviceType deviceType,String state, String name, LionPage lionPage) {

        Page<Device> page = deviceDao.deviceMonitorList(buildId, buildFloorId,deviceClassify ,deviceType, state, name, lionPage);
        List<ListDeviceMonitorVo> returnList = new ArrayList<>();
        List<Device> list = page.getContent();
        list.forEach(device -> {
            ListDeviceMonitorVo vo = new ListDeviceMonitorVo();
            vo.setBattery(device.getBattery());
            com.lion.core.Optional<Build> optional = buildExposeService.findById(device.getBuildId());
            vo.setBuildName(optional.isEmpty()?"":optional.get().getName());
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(device.getBuildFloorId());
            vo.setBuildFloorName(optionalBuildFloor.isEmpty()?"":optionalBuildFloor.get().getName());
            vo.setClassify(device.getDeviceClassify());
            vo.setCode(device.getCode());
            vo.setName(device.getName());
            vo.setDeviceId(device.getId());
//            DeviceGroupDevice deviceGroupDevice = deviceGroupDeviceExposeService.findByDeviceId(device.getId());
//            if (Objects.nonNull(deviceGroupDevice)) {
//                DeviceGroup deviceGroup = deviceGroupExposeService.findById(deviceGroupDevice.getDeviceGroupId());
//                Region region = regionExposeService.find(deviceGroup.getId());
//                if (Objects.nonNull(region)) {
//                    Department department = departmentExposeService.findById(region.departmentId);
//                    if (Objects.nonNull(department)) {
//                        vo.setDepartmentName(department.getName());
//                    }
//                }
//            }
            vo.setImg(device.getImg());
            vo.setImgUrl(fileExposeService.getUrl(device.getImg()));
            vo.setIsFault(device.getIsFault());
            vo.setState(device.getDeviceState());
            vo.setIsOnline(device.getIsOnline());
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public DetailsDeviceVo details(Long id) {
        com.lion.core.Optional<Device> optional = findById(id);
        DetailsDeviceVo detailsDeviceVo = new DetailsDeviceVo();
        if (optional.isEmpty()) {
            return detailsDeviceVo;
        }
        Device device = optional.get();
        BeanUtils.copyProperties(device,detailsDeviceVo);
        if (Objects.nonNull(device.getBuildId())){
            com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(device.getBuildId());
            if (optionalBuild.isPresent()){
                detailsDeviceVo.setBuildName(optionalBuild.get().getName());
            }
        }
        if (Objects.nonNull(device.getBuildFloorId())){
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(device.getBuildFloorId());
            if (optionalBuildFloor.isPresent()){
                BuildFloor buildFloor = optionalBuildFloor.get();
                detailsDeviceVo.setBuildFloorName(buildFloor.getName());
                detailsDeviceVo.setMapUrl(buildFloor.getMapUrl());
            }
        }
        detailsDeviceVo.setImgUrl(fileExposeService.getUrl(device.getImg()));
        if (Objects.nonNull(device.getRegionId())) {
            com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(device.getRegionId());
            if (optionalRegion.isPresent()) {
                Region region = optionalRegion.get();
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(region.getDepartmentId());
                if (optionalDepartment.isPresent()) {
                    Department department = optionalDepartment.get();
                    detailsDeviceVo.setDepartmentId(department.getId());
                    detailsDeviceVo.setDepartmentName(department.getName());
                }
            }

        }
        return detailsDeviceVo;
    }

    @Override
    public Integer countByDeviceStateIn(List<State> states) {
        return deviceDao.countByDeviceStateIn(states);
    }

    @Override
    public Integer countByIsFault(Boolean isFault) {
        return deviceDao.countByDeviceStateAndIsFault(State.USED,true);
    }

    @Override
    public Integer countByDeviceStateNotIn(List<State> states) {
        return deviceDao.countByDeviceStateNotIn(states);
    }

    @Override
    public Integer countOffLine() {
        return deviceDao.countByDeviceStateAndIsOnline(State.USED, false);
    }

    @Override
    public Integer countOnLine() {
        return deviceDao.countByDeviceStateAndIsOnline(State.USED, true);
    }

    @Override
    @Transactional
    public void replace(ReplaceDeviceDto replaceDeviceDto) {

    }

    @Override
    public Device save1(Device entity) {
        return super.save(entity);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW )
    public void  delete(Device device) {
        super.delete(device);
    }

    private DeviceStatisticsVo.DeviceStatisticsData count(DeviceClassify classify){
        DeviceStatisticsVo.DeviceStatisticsData data = new DeviceStatisticsVo.DeviceStatisticsData();
        data.setName(classify.getDesc());
        data.setCode(classify.getName());
        data.setCount(deviceDao.countByDeviceClassify(classify));
        return data;
    }
}
