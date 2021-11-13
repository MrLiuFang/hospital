package com.lion.manage.service.region.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.WarningBell;
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
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.BatchUpdateWashTemplateDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.region.vo.ListRegionVo;
import com.lion.manage.expose.ward.WardRoomExposeService;
import com.lion.manage.expose.ward.WardRoomSickbedExposeService;
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
import org.springframework.data.domain.PageImpl;
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
        wardRoomService.updateRegionId(addRegionDto.getWardRoomIds(),region.getId());
        wardRoomSickbedService.updateRegionId(addRegionDto.getWardRoomSickbedIds(),region.getId());
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
        wardRoomService.updateRegionId(updateRegionDto.getWardRoomIds(),region.getId());
        wardRoomSickbedService.updateRegionId(updateRegionDto.getWardRoomSickbedIds(),region.getId());
        deviceExposeService.relationRegion(region.getId(),updateRegionDto.getDeviceIds());
//        regionExposeObjectService.save(region.getId(),updateRegionDto.getExposeObjects());
        persistenceRedis(region, updateRegionDto.getDeviceIds(),getDeviceId(region.getId()), false);
    }

    @Override
    public void batchUpdateWashTemplate(BatchUpdateWashTemplateDto batchUpdateWashTemplateDto) {
        if (Objects.nonNull( batchUpdateWashTemplateDto.getRegionIds()) && batchUpdateWashTemplateDto.getRegionIds().size()>0) {
            batchUpdateWashTemplateDto.getRegionIds().forEach(id ->{
                Region region = findById(id);
                if (Objects.nonNull(region)) {
                    region.setWashTemplateId(batchUpdateWashTemplateDto.getWashTemplateId());
                    update(region);
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

    @Override
    public DetailsRegionVo details(Long id) {
        Region region = findById(id);
        if (Objects.isNull(region)) {
            return null;
        }
        DetailsRegionVo detailsRegionVo = new DetailsRegionVo();
        BeanUtils.copyProperties(region,detailsRegionVo);
        detailsRegionVo.setDevices(deviceExposeService.findByRegionId(region.getId()));
        detailsRegionVo.setWardRooms(wardRoomService.findByRegionId(region.getId()));
        detailsRegionVo.setWardRoomSickbeds(wardRoomSickbedService.findByRegionId(region.getId()));
        detailsRegionVo.setRegionType(regionTypeService.findById(region.getRegionTypeId()));
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
            warningBells.add(warningBellExposeService.findById(regionWarningBell.getWarningBellId()));
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
            Build build = buildService.findById(region.getBuildId());
            if (Objects.nonNull(build)){
                vo.setBuildName(build.getName());
            }
            BuildFloor buildFloor = buildFloorService.findById(region.getBuildFloorId());
            if (Objects.nonNull(buildFloor)){
                vo.setBuildFloorName(buildFloor.getName());
            }
            Department department = departmentService.findById(region.getDepartmentId());
            if (Objects.nonNull(department)){
                vo.setDepartmentName(department.getName());
            }
            if (Objects.nonNull(region.getWashTemplateId())) {
                vo.setWashTemplateVo(washTemplateService.details(region.getWashTemplateId()));
            }
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
