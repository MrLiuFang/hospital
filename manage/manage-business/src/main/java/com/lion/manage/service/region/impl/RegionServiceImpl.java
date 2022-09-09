package com.lion.manage.service.region.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.device.DeviceGroupDeviceExposeService;
import com.lion.device.expose.device.WarningBellExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.ward.WardRoomDao;
import com.lion.manage.dao.ward.WardRoomSickbedDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.BatchUpdateWashTemplateDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.region.vo.ListRegionVo;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.region.RegionTypeService;
import com.lion.manage.service.region.RegionWarningBellService;
import com.lion.manage.service.rule.WashTemplateService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired
    private WardRoomService wardRoomService;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

//    @Autowired
//    private RegionDeviceDao regionDeviceDao;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private RegionTypeService regionTypeService;

    @Autowired
    private WashTemplateService washTemplateService;

    @DubboReference
    private WarningBellExposeService  warningBellExposeService;

    @Override
    public List<Region> find(Long departmentId) {
        return regionDao.findByDepartmentId(departmentId);
    }

    @Override
    public Region findByName(String name) {
        return regionDao.findFirstByName(name);
    }

    @Override
    public Region findByCode(String code) {
        return regionDao.findFirstByCode(code);
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
        wardRoomService.updateRegionId(addRegionDto.getWardRoomIds(),region.getId(), addRegionDto.getBindType());
        wardRoomSickbedService.updateRegionId(addRegionDto.getWardRoomSickbedIds(),region.getId(), addRegionDto.getBindType());
        deviceExposeService.relationRegion(region.getId(),addRegionDto.getDeviceIds());
//        regionExposeObjectService.save(region.getId(),addRegionDto.getExposeObjects());
        persistenceRedis(region, addRegionDto.getDeviceIds(),null, false);
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
        wardRoomService.updateRegionId(updateRegionDto.getWardRoomIds(),region.getId(), updateRegionDto.getBindType());
        wardRoomSickbedService.updateRegionId(updateRegionDto.getWardRoomSickbedIds(),region.getId(), updateRegionDto.getBindType());
        deviceExposeService.relationRegion(region.getId(),updateRegionDto.getDeviceIds());
//        regionExposeObjectService.save(region.getId(),updateRegionDto.getExposeObjects());
        persistenceRedis(region, updateRegionDto.getDeviceIds(),getDeviceId(region.getId()), false);
    }

    @Override
    public void batchUpdateWashTemplate(BatchUpdateWashTemplateDto batchUpdateWashTemplateDto) {
        if (Objects.nonNull( batchUpdateWashTemplateDto.getRegionIds()) && batchUpdateWashTemplateDto.getRegionIds().size()>0) {
            batchUpdateWashTemplateDto.getRegionIds().forEach(id ->{
                com.lion.core.Optional<Region> optional = findById(id);
                if (optional.isPresent()) {
                    Region region = optional.get();
                    redisTemplate.delete(RedisConstants.REGION+region.getId());
                    if (Objects.nonNull(batchUpdateWashTemplateDto.getWashTemplateId())) {
                        region.setWashTemplateId(batchUpdateWashTemplateDto.getWashTemplateId());
                        update(region);
                    }else {
                        regionDao.updateWashTemplateId(region.getId());
                    }

                }
            });
        }
    }

    @Override
    @Transactional
    public void updateCoordinates(UpdateRegionCoordinatesDto updateRegionCoordinatesDto) {
        if (Objects.isNull(updateRegionCoordinatesDto.getId())){
            BusinessException.throwException(MessageI18nUtil.getMessage("0000000"));
        }
        com.lion.core.Optional<Region> optional = findById(updateRegionCoordinatesDto.getId());
        if (optional.isPresent()) {
            Region region = optional.get();
            region.setCoordinates(updateRegionCoordinatesDto.getCoordinates());
            update(region);
            redisTemplate.opsForValue().set(RedisConstants.REGION + region.getId(), region, 5, TimeUnit.MINUTES);
        }
    }

    @Override
//    @GlobalTransactional
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            com.lion.core.Optional<Region> optional = findById(deleteDto.getId());
            deleteById(deleteDto.getId());
            List<RegionCctv> listreRegionCctvs = regionCctvDao.findByRegionId(deleteDto.getId());
            List<Long> oldCctvIds = new ArrayList<>();
            listreRegionCctvs.forEach(regionCctv -> {
                oldCctvIds.add(regionCctv.getCctvId());
            });
            regionCctvDao.deleteByRegionId(deleteDto.getId());
            wardRoomDao.updateRegionIdIsNull(deleteDto.getId());
            wardRoomSickbedDao.updateRegionIdIsNull(deleteDto.getId());
            if (optional.isPresent()){
                Region region = optional.get();
                cctvExposeService.relationPosition(oldCctvIds,new ArrayList<Long>(),region.getBuildId(),region.getBuildFloorId(),deleteDto.getId(), region.getDepartmentId());
                deviceExposeService.relationRegion(region.getId(),new ArrayList<Long>());
//            regionExposeObjectDao.deleteByRegionId(deleteDto.getId());
                persistenceRedis(region,  null,getDeviceId(region.getId()), true);
            }
        });
    }

    @Override
    public DetailsRegionVo details(Long id) {
        com.lion.core.Optional<Region> optional = findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Region region = optional.get();
        DetailsRegionVo detailsRegionVo = new DetailsRegionVo();
        BeanUtils.copyProperties(region,detailsRegionVo);
        Optional<Department> departmentOptional = departmentService.findById(region.departmentId);
        if (departmentOptional.isPresent()){
            detailsRegionVo.setDepartmentName(departmentOptional.get().getName());
        }
        detailsRegionVo.setDevices(deviceExposeService.findByRegionId(region.getId()));
        detailsRegionVo.setWardRooms(wardRoomService.findByRegionId(region.getId()));
        detailsRegionVo.setWardRoomSickbeds(wardRoomSickbedService.findByRegionId(region.getId()));
        com.lion.core.Optional<RegionType> optionalRegionType  = regionTypeService.findById(region.getRegionTypeId());
        detailsRegionVo.setRegionType(optionalRegionType.isPresent()?optionalRegionType.get():null);
        List<RegionCctv> list = regionCctvService.find(region.getId());
        List<Long> cctvIds = new ArrayList<>();
        list.forEach(regionCctv -> {
            cctvIds.add(regionCctv.getCctvId());
        });
        if (cctvIds.size()>0) {
            detailsRegionVo.setCctvs(cctvExposeService.find(cctvIds));
        }
//            List<RegionExposeObject> regionExposeObjectList = regionExposeObjectService.find(region.getId());
//            List<ExposeObject> exposeObjectList = new ArrayList<>();
//            regionExposeObjectList.forEach(regionExposeObject -> {
//                exposeObjectList.add(regionExposeObject.getExposeObject());
//            });
//            detailsRegionVo.setExposeObjects(exposeObjectList);
        detailsRegionVo.setWashTemplateVo(washTemplateService.details(region.getWashTemplateId()));
        List<RegionWarningBell> regionWarningBells = regionWarningBellService.find(region.getId());
        List<WarningBell> warningBells = new ArrayList<>();
        regionWarningBells.forEach(regionWarningBell -> {
            com.lion.core.Optional<WarningBell> optional1 = warningBellExposeService.findById(regionWarningBell.getWarningBellId());
            warningBells.add(optional1.isPresent()?optional1.get():null);
        });
        detailsRegionVo.setWarningBells(warningBells);
        return detailsRegionVo;
    }

    @Override
    public IPageResultData<List<ListRegionVo>> list(String name, String code, List<Long> departmentIds, Long washTemplateId, Long regionTypeId, Long buildId, Long buildFloorId, LionPage lionPage) {
        ResultData resultData = ResultData.instance();
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        }
        if (Objects.nonNull(buildId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildId",buildId);
        }
        if (Objects.nonNull(buildFloorId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_buildFloorId",buildFloorId);
        }
        if (Objects.nonNull(washTemplateId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_washTemplateId",washTemplateId);
        }
        if (Objects.nonNull(regionTypeId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_regionTypeId",regionTypeId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Region> page = findNavigator(lionPage);
        List<Region> list = page.getContent();
        List<ListRegionVo> returnList = new ArrayList<>();
        list.forEach(region -> {
            ListRegionVo vo = new ListRegionVo();
            BeanUtils.copyProperties(region,vo);
            com.lion.core.Optional<Build> optionalBuild = buildService.findById(region.getBuildId());
            if (optionalBuild.isPresent()){
                vo.setBuildName(optionalBuild.get().getName());
            }
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(region.getBuildFloorId());
            if (optionalBuildFloor.isPresent()){
                vo.setBuildFloorName(optionalBuildFloor.get().getName());
            }
            com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(region.getDepartmentId());
            if (optionalDepartment.isPresent()){
                vo.setDepartmentName(optionalDepartment.get().getName());
            }
            if (Objects.nonNull(region.getWashTemplateId())) {
                vo.setWashTemplateVo(washTemplateService.details(region.getWashTemplateId()));
            }
            com.lion.core.Optional<RegionType> optionalRegionType = regionTypeService.findById(region.getRegionTypeId());
            vo.setRegionType(optionalRegionType.isPresent()?optionalRegionType.get():null);
            vo.setDevices(deviceExposeService.findByRegionIdAndDeviceClassify(region.getId(), DeviceClassify.HAND_WASHING));
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    private List<Long> getDeviceId(Long regionId) {
        List<Long> devideIds = new ArrayList<>();
        List<Device> list = deviceExposeService.findByRegionId(regionId);
        list.forEach(device -> {
            devideIds.add(device.getId());
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
        com.lion.core.Optional<Build> optional = buildService.findById(buildId);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000070"));
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        com.lion.core.Optional<Department> optional = departmentService.findById(departmentId);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000069"));
        }
    }

    private void assertBuildFloorExist(Long buildId,Long buildFloorId) {
        com.lion.core.Optional<BuildFloor> optional = buildFloorService.findById(buildFloorId);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000071"));
        }
        if (optional.isPresent()) {
            if (!Objects.equals(optional.get().getBuildId(), buildId)) {
                BusinessException.throwException(MessageI18nUtil.getMessage("2000082"));
            }
        }
    }

    private void persistenceRedis(Region region, List<Long> deviceIds,List<Long> oldDeviceIds,Boolean isDelete){
//        if (Objects.nonNull(oldDevideGroupId)){
//            List<DeviceGroupDevice> export = deviceGroupDeviceExposeService.find(oldDevideGroupId);
//            export.forEach(deviceGroupDevice -> {
//                redisTemplate.delete(RedisConstants.DEVICE_REGION+deviceGroupDevice.getDeviceId());
//            });
//        }
//        if (Objects.nonNull(devideGroupId)) {
//            List<DeviceGroupDevice> export = deviceGroupDeviceExposeService.find(devideGroupId);
//            export.forEach(deviceGroupDevice -> {
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
