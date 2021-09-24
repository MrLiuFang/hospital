package com.lion.manage.service.region.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionDeviceDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.RegionDevice;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.region.RegionWarningBellService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:08
 */
@Service
public class RegionServiceImpl extends BaseServiceImpl<Region> implements RegionService {

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionCctvService regionCctvService;

//    @Autowired
//    private RegionExposeObjectService regionExposeObjectService;

    @Autowired
    private RegionCctvDao regionCctvDao;

//    @Autowired
//    private RegionExposeObjectDao regionExposeObjectDao;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private WardRoomDao wardRoomDao;

    @Autowired
    private WardRoomSickbedDao wardRoomSickbedDao;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

    @Autowired
    private RegionWarningBellService regionWarningBellService;

    @DubboReference
    private WardRoomExposeService wardRoomExposeService;

    @DubboReference
    private WardRoomSickbedExposeService wardRoomSickbedExposeService;

    @Autowired
    private RegionDeviceDao regionDeviceDao;

    @Override
    public List<Region> find(Long departmentId) {
        return regionDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<Region> findByBuildFloorId(Long buildFloorId) {
        return regionDao.findByBuildFloorId(buildFloorId);
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public void add(AddRegionDto addRegionDto) {
        Region region = new Region();
        BeanUtils.copyProperties(addRegionDto,region);
        assertBuildExist(region.getBuildId());
        assertBuildFloorExist(region.getBuildId(),region.getBuildFloorId());
        assertDepartmentExist(region.departmentId);
        assertNameExist(region.getName(),null);
//        assertDeviceGroupIsUse(region.getDeviceGroupId(),null);
//        if (addRegionDto.isPublic && (Objects.isNull(addRegionDto.getExposeObjects()) ||addRegionDto.getExposeObjects().size()<=0) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000078"));
//        }
        region = save(region);
        regionCctvService.save(region.getId(),addRegionDto.getCctvIds());
        regionWarningBellService.add(addRegionDto.getWarningBellIds(),region.getId());
        wardRoomExposeService.updateRegionId(addRegionDto.wardRoomIds,region.getId());
        wardRoomSickbedExposeService.updateRegionId(addRegionDto.getWardRoomSickbedIds(),region.getId());
//        regionExposeObjectService.save(region.getId(),addRegionDto.getExposeObjects());
        persistenceRedis(region, addRegionDto.deviceIds,null, false);
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public void update(UpdateRegionDto updateRegionDto) {
        Region region = new Region();
        BeanUtils.copyProperties(updateRegionDto,region);
//        assertBuildExist(region.getBuildId());
//        assertBuildFloorExist(region.getBuildId(),region.getBuildFloorId());
        assertDepartmentExist(region.departmentId);
        assertNameExist(region.getName(),region.getId());
//        assertDeviceGroupIsUse(region.getDeviceGroupId(),region.getId());
//        if (updateRegionDto.isPublic && (Objects.isNull(updateRegionDto.getExposeObjects()) ||updateRegionDto.getExposeObjects().size()<=0) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000078"));
//        }
        update(region);
        regionCctvService.save(region.getId(),updateRegionDto.getCctvIds());
        regionWarningBellService.add(updateRegionDto.getWarningBellIds(),region.getId());
        wardRoomExposeService.updateRegionId(updateRegionDto.wardRoomIds,region.getId());
        wardRoomSickbedExposeService.updateRegionId(updateRegionDto.getWardRoomSickbedIds(),region.getId());
//        regionExposeObjectService.save(region.getId(),updateRegionDto.getExposeObjects());
        persistenceRedis(region, updateRegionDto.deviceIds,getDeviceId(region.getId()), false);
    }

    @Override
    @Transactional
    public void updateCoordinates(UpdateRegionCoordinatesDto updateRegionCoordinatesDto) {
        if (Objects.isNull(updateRegionCoordinatesDto.getId())){
            BusinessException.throwException(MessageI18nUtil.getMessage("0000000"));
        }
        Region region = findById(updateRegionCoordinatesDto.getId());
        region.setCoordinates(updateRegionCoordinatesDto.getCoordinates());
        update(region);
        redisTemplate.opsForValue().set(RedisConstants.REGION + region.getId(), region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            Region region = findById(deleteDto.getId());
            deleteById(deleteDto.getId());
            List<RegionCctv> listreRegionCctvs = regionCctvDao.findByRegionId(deleteDto.getId());
            List<Long> oldCctvIds = new ArrayList<>();
            listreRegionCctvs.forEach(regionCctv -> {
                oldCctvIds.add(regionCctv.getCctvId());
            });
            if (Objects.nonNull(region)){
                cctvExposeService.relationPosition(oldCctvIds,new ArrayList<Long>(),region.getBuildId(),region.getBuildFloorId(),deleteDto.getId(), region.getDepartmentId());
            }
            regionCctvDao.deleteByRegionId(deleteDto.getId());
            wardRoomDao.updateRegionIdIsNull(deleteDto.getId());
            wardRoomSickbedDao.updateRegionIdIsNull(deleteDto.getId());
//            regionExposeObjectDao.deleteByRegionId(deleteDto.getId());
            persistenceRedis(region,  null,getDeviceId(region.getId()), true);
        });
    }

    private List<Long> getDeviceId(Long regionId) {
        List<Long> devideIds = new ArrayList<>();
        List<RegionDevice> list = regionDeviceDao.findByRegionId(regionId);
        list.forEach(regionDevice -> {
            devideIds.add(regionDevice.getDeviceId());
        });
        return devideIds;
    }

    private void assertNameExist(String name, Long id) {
        Region region = regionDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(region)) || (Objects.nonNull(id) && Objects.nonNull(region) && !Objects.equals(region.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000080"));
        }
    }

//    private void assertDeviceGroupIsUse(Long deviceGroupId, Long id) {
//        if (Objects.isNull(deviceGroupId)){
//            return;
//        }
//        Region region = regionDao.findFirstByDeviceGroupId(deviceGroupId);
//        if ((Objects.isNull(id) && Objects.nonNull(region)) || (Objects.nonNull(id) && Objects.nonNull(region) && !Objects.equals(region.getId(),id)) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000081"));
//        }
//    }

    private void assertBuildExist(Long buildId) {
        Build build = buildService.findById(buildId);
        if (Objects.isNull(build)){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000070"));
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        Department department = departmentService.findById(departmentId);
        if (Objects.isNull(department)){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000069"));
        }
    }

    private void assertBuildFloorExist(Long buildId,Long buildFloorId) {
        BuildFloor buildFloor = buildFloorService.findById(buildFloorId);
        if (Objects.isNull(buildFloor)){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000071"));
        }
        if (!Objects.equals(buildFloor.getBuildId(),buildId)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("2000082"));
        }
    }

    private void persistenceRedis(Region region, List<Long> deviceIds,List<Long> oldDeviceIds,Boolean isDelete){
//        if (Objects.nonNull(oldDevideGroupId)){
//            List<DeviceGroupDevice> list = deviceGroupDeviceExposeService.find(oldDevideGroupId);
//            list.forEach(deviceGroupDevice -> {
//                redisTemplate.delete(RedisConstants.DEVICE_REGION+deviceGroupDevice.getDeviceId());
//            });
//        }
//        if (Objects.nonNull(devideGroupId)) {
//            List<DeviceGroupDevice> list = deviceGroupDeviceExposeService.find(devideGroupId);
//            list.forEach(deviceGroupDevice -> {
//                redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION + deviceGroupDevice.getDeviceId(), region.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            });
//        }
//        redisTemplate.delete(RedisConstants.REGION_EXPOSE_OBJECT+region.getId());
//        if (Objects.nonNull(exposeObjects) && exposeObjects.size()>0){
//            exposeObjects.forEach(exposeObject -> {
//                redisTemplate.opsForList().leftPush(RedisConstants.REGION_EXPOSE_OBJECT+region.getId(),exposeObject);
//                redisTemplate.expire(RedisConstants.REGION_EXPOSE_OBJECT+region.getId(), RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//            });
//        }
        if (Objects.nonNull(oldDeviceIds) && oldDeviceIds.size()>0) {
            oldDeviceIds.forEach(id -> {
                redisTemplate.delete(RedisConstants.DEVICE_REGION + id);
            });
        }

        if (Objects.nonNull(deviceIds) && deviceIds.size()>0) {
            deviceIds.forEach(id -> {
                redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION + id, region.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            });
        }
        if (Objects.equals(true,isDelete)) {
            redisTemplate.delete(RedisConstants.REGION_WASH_TEMPLATE + region.getId());
            redisTemplate.delete(RedisConstants.REGION + region.getId());
            redisTemplate.delete(RedisConstants.REGION_BUILD + region.getId());
            redisTemplate.delete(RedisConstants.REGION_BUILD_FLOOR + region.getId());
            redisTemplate.delete(RedisConstants.REGION_DEPARTMENT + region.getId());
        }else {
            redisTemplate.opsForValue().set(RedisConstants.REGION_WASH_TEMPLATE + region.getId(), region.getWashTemplateId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.REGION + region.getId(), region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.REGION_BUILD + region.getId(), region.getBuildId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.REGION_BUILD_FLOOR + region.getId(), region.getBuildFloorId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.REGION_DEPARTMENT + region.getId(), region.getDepartmentId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
    }
}
