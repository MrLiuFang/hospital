package com.lion.manage.service.region.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionExposeObjectDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.ExposeObject;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionExposeObjectService;
import com.lion.manage.service.region.RegionService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    private RegionExposeObjectService regionExposeObjectService;

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Autowired
    private RegionExposeObjectDao regionExposeObjectDao;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private WardRoomDao wardRoomDao;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private DeviceGroupDeviceExposeService deviceGroupDeviceExposeService;

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
        assertDeviceGroupIsUse(region.getDeviceGroupId(),null);
        if (addRegionDto.isPublic && (Objects.isNull(addRegionDto.getExposeObjects()) ||addRegionDto.getExposeObjects().size()<=0) ){
            BusinessException.throwException("请选择公开对象");
        }
        region = save(region);
        regionCctvService.save(region.getId(),addRegionDto.getCctvIds());
        regionExposeObjectService.save(region.getId(),addRegionDto.getExposeObjects());
        persistenceRedis(region,addRegionDto.getExposeObjects(), region.getDeviceGroupId(),null);
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public void update(UpdateRegionDto updateRegionDto) {
        Region oldRegion = findById(updateRegionDto.getId());
        Long oldDevideGroupId = null;
        if (Objects.nonNull(oldRegion)){
            oldDevideGroupId = oldRegion.getDeviceGroupId();
        }else {
            BusinessException.throwException("更新的数据不存在");
        }
        Region region = new Region();
        BeanUtils.copyProperties(updateRegionDto,region);
        assertBuildExist(region.getBuildId());
        assertBuildFloorExist(region.getBuildId(),region.getBuildFloorId());
        assertDepartmentExist(region.departmentId);
        assertNameExist(region.getName(),region.getId());
        assertDeviceGroupIsUse(region.getDeviceGroupId(),null);
        if (updateRegionDto.isPublic && (Objects.isNull(updateRegionDto.getExposeObjects()) ||updateRegionDto.getExposeObjects().size()<=0) ){
            BusinessException.throwException("请选择公开对象");
        }
        update(region);
        regionCctvService.save(region.getId(),updateRegionDto.getCctvIds());
        regionExposeObjectService.save(region.getId(),updateRegionDto.getExposeObjects());
        persistenceRedis(region,updateRegionDto.getExposeObjects(), region.getDeviceGroupId(),oldDevideGroupId);
    }

    @Override
    @Transactional
    public void updateCoordinates(UpdateRegionCoordinatesDto updateRegionCoordinatesDto) {
        Region region = new Region();
        BeanUtils.copyProperties(updateRegionCoordinatesDto,region);
        if (Objects.isNull(region.getId())){
            BusinessException.throwException("id不能为空");
        }
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
            regionExposeObjectDao.deleteByRegionId(deleteDto.getId());
            wardRoomDao.deleteByWardId(deleteDto.getId());
            persistenceRedis(region, Collections.EMPTY_LIST, null,region.getDeviceGroupId());
        });
    }

    private void assertNameExist(String name, Long id) {
        Region region = regionDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(region)) || (Objects.nonNull(id) && Objects.nonNull(region) && !Objects.equals(region.getId(),id)) ){
            BusinessException.throwException("该区域名称已存在");
        }
    }

    private void assertDeviceGroupIsUse(Long deviceGroupId, Long id) {
        if (Objects.isNull(deviceGroupId)){
            return;
        }
        Region region = regionDao.findFirstByDeviceGroupId(deviceGroupId);
        if ((Objects.isNull(id) && Objects.nonNull(region)) || (Objects.nonNull(id) && Objects.nonNull(region) && !Objects.equals(region.getId(),id)) ){
            BusinessException.throwException("该设备组在其它区域已经使用");
        }
    }

    private void assertBuildExist(Long buildId) {
        Build build = buildService.findById(buildId);
        if (Objects.isNull(build)){
            BusinessException.throwException("建筑不存在");
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        Department department = departmentService.findById(departmentId);
        if (Objects.isNull(department)){
            BusinessException.throwException("科室不存在");
        }
    }

    private void assertBuildFloorExist(Long buildId,Long buildFloorId) {
        BuildFloor buildFloor = buildFloorService.findById(buildFloorId);
        if (Objects.isNull(buildFloor)){
            BusinessException.throwException("建筑楼层不存在");
        }
        if (!Objects.equals(buildFloor.getBuildId(),buildId)) {
            BusinessException.throwException("该建筑不存在此楼层");
        }
    }

    private void persistenceRedis(Region region, List<ExposeObject> exposeObjects,Long devideGroupId, Long oldDevideGroupId){
        if (Objects.nonNull(oldDevideGroupId)){
            List<DeviceGroupDevice> list = deviceGroupDeviceExposeService.find(oldDevideGroupId);
            list.forEach(deviceGroupDevice -> {
                redisTemplate.delete(RedisConstants.DEVICE_REGION+deviceGroupDevice.getDeviceId());
            });
        }
        if (Objects.nonNull(devideGroupId)) {
            List<DeviceGroupDevice> list = deviceGroupDeviceExposeService.find(devideGroupId);
            list.forEach(deviceGroupDevice -> {
                redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION + deviceGroupDevice.getDeviceId(), region.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            });
        }
        redisTemplate.delete(RedisConstants.REGION_EXPOSE_OBJECT+region.getId());
        if (Objects.nonNull(exposeObjects) && exposeObjects.size()>0){
            exposeObjects.forEach(exposeObject -> {
                redisTemplate.opsForList().leftPush(RedisConstants.REGION_EXPOSE_OBJECT+region.getId(),exposeObject);
                redisTemplate.expire(RedisConstants.REGION_EXPOSE_OBJECT+region.getId(), RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
            });
        }
        redisTemplate.opsForValue().set(RedisConstants.REGION + region.getId(), region, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.REGION_BUILD + region.getId(), region.getBuildId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.REGION_BUILD_FLOOR + region.getId(), region.getBuildFloorId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.REGION_DEPARTMENT + region.getId(), region.getDepartmentId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }
}
