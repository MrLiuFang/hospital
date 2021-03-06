package com.lion.manage.service.rule.impl;
//
//import com.lion.common.constants.RedisConstants;
//import com.lion.common.expose.file.FileExposeService;
//import com.lion.core.common.dto.DeleteDto;
//import com.lion.core.service.impl.BaseServiceImpl;
//import com.lion.device.entity.device.Device;
//import com.lion.device.expose.device.DeviceExposeService;
//import com.lion.exception.BusinessException;
//import com.lion.manage.dao.rule.WashDao;
//import com.lion.manage.dao.rule.WashRegionDao;
//import com.lion.manage.dao.rule.WashUserDao;
//import com.lion.manage.entity.department.Department;
//import com.lion.manage.entity.enums.WashRuleType;
//import com.lion.manage.entity.region.Region;
//import com.lion.manage.entity.rule.Wash;
//import com.lion.manage.entity.rule.WashDevice;
//import com.lion.manage.entity.rule.WashRegion;
//import com.lion.manage.entity.rule.WashUser;
//import com.lion.manage.entity.rule.dto.AddWashDto;
//import com.lion.manage.entity.rule.dto.UpdateWashDto;
//import com.lion.manage.entity.rule.vo.DetailsWashVo;
//import com.lion.manage.service.build.BuildFloorService;
//import com.lion.manage.service.build.BuildService;
//import com.lion.manage.service.department.DepartmentService;
//import com.lion.manage.service.department.DepartmentUserService;
//import com.lion.manage.service.region.RegionService;
//import com.lion.manage.service.rule.*;
//import com.lion.upms.entity.user.User;
//import com.lion.upms.expose.user.UserExposeService;
//import com.lion.upms.expose.user.UserTypeExposeService;
//import com.lion.utils.MessageI18nUtil;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author Mr.Liu
// * @Description:
// * @date 2021/4/9??????4:55
// */
//@Service
//public class WashServiceImpl extends BaseServiceImpl<Wash> implements WashService {
//
//    @Autowired
//    private WashDao washDao;
//
//    @Autowired
//    private WashRegionDao washRegionDao;
//
//    @Autowired
//    private WashUserDao washUserDao;
//
//    @Autowired
//    private WashDeviceService washDeviceService;
//
//    @Autowired
//    private WashRegionService washRegionService;
//
//    @Autowired
//    private WashUserServcie washUserService;
//
//    @Autowired
//    private RegionService regionService;
//
//
//    @DubboReference
//    private UserExposeService userExposeService;
//
//    @Autowired
//    private DepartmentService departmentService;
//
//    @DubboReference
//    private FileExposeService fileExposeService;
//
//    @DubboReference
//    private DeviceExposeService deviceExposeService;
//
//    @Autowired
//    private BuildService buildService;
//
//    @Autowired
//    private BuildFloorService buildFloorService;
//
//    @Autowired
//    private DepartmentUserService departmentUserService;
//
//    @Autowired
//    private WashDeviceTypeService washDeviceTypeService;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @DubboReference
//    private UserTypeExposeService userTypeExposeService;
//
//    @Override
//    @Transactional
//    public void add(AddWashDto addWashDto) {
//        Wash wash = new Wash();
//        BeanUtils.copyProperties(addWashDto,wash);
//        assertNameExist(wash.getName(),null);
//        assertEnteringTime(wash,false);
//        if (Objects.equals(addWashDto.getType(),WashRuleType.LOOP)) {
//            assertLoopWashExist(wash.getIsAllUser(), wash.getId());
//        }
//        wash = save(wash);
//        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
//            washRegionService.add(addWashDto.getRegionId(),wash.getId());
//            washDeviceService.add(addWashDto.getDeviceId(),wash.getId());
//        }
//        if (Objects.isNull(wash.getIsAllUser()) || Objects.equals(wash.getIsAllUser(),false)){
//            washUserService.add(addWashDto.getUserId(),wash);
//        }
//        if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.nonNull(addWashDto.getDeviceTypes()) && addWashDto.getDeviceTypes().size()>0){
//            washDeviceTypeService.add(wash.getId(),addWashDto.getDeviceTypes());
//        }
//        persistence2Redis(addWashDto.getRegionId(),addWashDto.getUserId(),addWashDto.getDeviceId(),wash,false);
//    }
//
//    @Override
//    @Transactional
//    public void update(UpdateWashDto updateWashDto) {
//        Wash wash = new Wash();
//        BeanUtils.copyProperties(updateWashDto,wash);
//        assertNameExist(wash.getName(),wash.getId());
//        assertEnteringTime(wash,true);
//        if (Objects.equals(updateWashDto.getType(),WashRuleType.LOOP)) {
//            assertLoopWashExist(wash.getIsAllUser(), wash.getId());
//        }
//        update(wash);
//        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
//            washRegionService.add(updateWashDto.getRegionId(),wash.getId());
//            washDeviceService.add(updateWashDto.getDeviceId(),wash.getId());
//        }
//        if (Objects.isNull(wash.getIsAllUser()) || Objects.equals(wash.getIsAllUser(),false)){
//            washUserService.add(updateWashDto.getUserId(),wash);
//        }
//        if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.nonNull(updateWashDto.getDeviceTypes()) && updateWashDto.getDeviceTypes().size()>0){
//            washDeviceTypeService.add(wash.getId(),updateWashDto.getDeviceTypes());
//        }
//
//        persistence2Redis(updateWashDto.getRegionId(),updateWashDto.getUserId(),updateWashDto.getDeviceId(),wash,false);
//    }
//
//    @Override
//    public DetailsWashVo details(Long id) {
//        Wash wash = this.findById(id);
//        if (Objects.isNull(wash)){
//            return null;
//        }
//        DetailsWashVo detailsWashVo = new DetailsWashVo();
//        BeanUtils.copyProperties(wash,detailsWashVo);
//        List<WashDevice> washDeviceList = washDeviceService.find(wash.getId());
//        if (Objects.nonNull(washDeviceList) && washDeviceList.size()>0){
//            List<DetailsWashVo.DeviceVo> vos = new ArrayList<DetailsWashVo.DeviceVo>();
//            washDeviceList.forEach(washDevice->{
//                Device device = deviceExposeService.findById(washDevice.getDeviceId());
//                if (Objects.nonNull(device)){
//                    DetailsWashVo.DeviceVo vo = new DetailsWashVo.DeviceVo();
//                    BeanUtils.copyProperties(device,vo);
//                    vos.add(vo);
//                }
//            });
//            detailsWashVo.setDeviceVos(vos);
//        }
//        List<WashRegion>  washRegions = washRegionService.find(wash.getId());
//        if (Objects.nonNull(washRegions) && washRegions.size()>0){
//            List<Region> regionVos = new ArrayList<Region>();
//            washRegions.forEach(washRegion->{
//                Region region = regionService.findById(washRegion.getRegionId());
//                if (Objects.nonNull(region)){
////                    DetailsWashVo.RegionVo regionVo = new DetailsWashVo.RegionVo();
////                    regionVo.setRegionName(region.getName());
////                    regionVo.setRemarks(region.getRemarks());
////                    regionVo.setId(region.getId());
////                    Build build = buildService.findById(region.getBuildId());
////                    if (Objects.nonNull(build)){
////                        regionVo.setBuildName(build.getName());
////                    }
////                    BuildFloor buildFloor = buildFloorService.findById(region.getBuildFloorId());
////                    if (Objects.nonNull(buildFloor)){
////                        regionVo.setBuildFloorName(buildFloor.getName());
////                    }
////                    regionVos.add(regionVo);
//                    regionVos.add(region);
//                }
//            });
//            detailsWashVo.setRegionVos(regionVos);
//        }
//        List<WashUser> washUsers = washUserService.find(wash.getId());
//        if (Objects.nonNull(washUsers) &&  washUsers.size()>0){
//            List<DetailsWashVo.UserVo> userVos = new ArrayList<>();
//            washUsers.forEach(washUser -> {
//                User user = userExposeService.findById(washUser.getUserId());
//                if (Objects.nonNull(user)){
//                    DetailsWashVo.UserVo userVo = new DetailsWashVo.UserVo();
//                    userVo.setId(user.getId());
//                    Department department = departmentUserService.findDepartment(user.getId());
//                    if (Objects.nonNull(department)){
//                        userVo.setDepartmentName(department.getName());
//                    }
//                    userVo.setName(user.getName());
//                    userVo.setNumber(user.getNumber());
//                    userVo.setTagCode(user.getTagCode());
//                    if (Objects.nonNull(user.getUserTypeId())) {
//                        userVo.setUserType(userTypeExposeService.findById(user.getUserTypeId()));
//                    }
//                    userVos.add(userVo);
//                }
//            });
//            detailsWashVo.setUserVos(userVos);
//            detailsWashVo.setWashDeviceTypes(washDeviceTypeService.find(wash.getId()));
//        }
//        return detailsWashVo;
//    }
//
//    @Override
//    @Transactional
//    public void delete(List<DeleteDto> deleteDtos) {
//        deleteDtos.forEach(deleteDto -> {
//            persistence2Redis(Collections.EMPTY_LIST,Collections.EMPTY_LIST,Collections.EMPTY_LIST,this.findById(deleteDto.getId()),true);
//            this.deleteById(deleteDto.getId());
//            washDeviceService.delete(deleteDto.getId());
//            washRegionService.delete(deleteDto.getId());
//            washUserService.delete(deleteDto.getId());
//            washDeviceTypeService.add(deleteDto.getId(),null);
//        });
//    }
//
//    private void assertLoopWashExist(Boolean isAllUser, Long id) {
////        List<WashUser> washUserList = washUserDao.find(WashRuleType.LOOP,true);
////        if (Objects.nonNull(washUserList) && washUserList.size()>0) {
////            if (Objects.equals(true,isAllUser)) {
////                BusinessException.throwException("??????????????????????????????????????????????????????,????????????????????????????????????????????????,???????????????????????????");
////            }
////        }
//        List<Wash> export = washDao.findFirstByTypeAndIsAllUser(WashRuleType.LOOP, true);
//        Wash washLoppAllUser = export.size()>0?export.get(0):null;
//        if ((Objects.isNull(id) && Objects.nonNull(washLoppAllUser) ) || (Objects.nonNull(id) && Objects.nonNull(washLoppAllUser) &&  !Objects.equals(washLoppAllUser.getId(),id)) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000087"));
//        }
//
//        if (Objects.nonNull(washLoppAllUser) && Objects.equals(false,isAllUser)){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000088"));
//        }
//
//    }
//
//    private void assertEnteringTime(Wash wash,Boolean isUpdate) {
//        if (Objects.nonNull(wash) &&Objects.equals(wash.getType(),WashRuleType.LOOP)){
//            return;
//        }
////        if (Objects.nonNull(wash.getAfterEnteringTime()) && Objects.nonNull(wash.getBeforeEnteringTime())) {
////            BusinessException.throwException(MessageI18nUtil.getMessage("2000089"));
////        }
////        if (Objects.isNull(wash.getAfterEnteringTime()) && Objects.isNull(wash.getBeforeEnteringTime())) {
////            BusinessException.throwException(MessageI18nUtil.getMessage("2000090"));
////        }
////        if (Objects.nonNull(wash.getAfterEnteringTime())) {
////            if (wash.getAfterEnteringTime()<=0){
////                BusinessException.throwException(MessageI18nUtil.getMessage("2000091"));
////            }
////            wash.setBeforeEnteringTime(null);
////            if (Objects.equals(true,isUpdate)) {
////                washDao.setBeforeEnteringTime(wash.getId());
////            }
////        }else if (Objects.nonNull(wash.getBeforeEnteringTime())) {
////            if (wash.getBeforeEnteringTime()<=0){
////                BusinessException.throwException(MessageI18nUtil.getMessage("2000092"));
////            }
////            wash.setAfterEnteringTime(null);
////            if (Objects.equals(true,isUpdate)) {
////                washDao.setAfterEnteringTimeIsNull(wash.getId());
////            }
////        }
//    }
//
//    private void assertNameExist(String name, Long id) {
//        Wash wash = washDao.findFirstByName(name);
//        if ((Objects.isNull(id) && Objects.nonNull(wash) ) || (Objects.nonNull(id) && Objects.nonNull(wash) &&  !Objects.equals(wash.getId(),id)) ){
//            BusinessException.throwException(MessageI18nUtil.getMessage("2000093"));
//        }
//    }
//
//    private void persistence2Redis(List<Long> regionIds,List<Long> userIds,List<Long> deviceIds,Wash wash,Boolean isDelete){
//        redisTemplate.delete(RedisConstants.WASH_DEVICE+wash.getId());
//        if (Objects.equals(false,isDelete)){
//            if (Objects.nonNull(deviceIds) && deviceIds.size()>0) {
//                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE + wash.getId(), deviceIds);
//                redisTemplate.expire(RedisConstants.WASH_DEVICE + wash.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            }
//        }
//
//        if (Objects.equals(wash.getType(),WashRuleType.LOOP)) {
//            if (Objects.nonNull(wash.getIsAllUser()) && wash.getIsAllUser()){
//                List<Long> export = redisTemplate.opsForList().range(RedisConstants.ALL_USER_LOOP_WASH,0,-1);
//                if (Objects.isNull(export)){
//                    export = new ArrayList<Long>();
//                }
//                export.remove(wash.getId());
//                redisTemplate.delete(RedisConstants.ALL_USER_LOOP_WASH);
//                if (Objects.nonNull(export) && export.size()>0) {
//                    redisTemplate.opsForList().leftPushAll(RedisConstants.ALL_USER_LOOP_WASH, export);
//                    redisTemplate.expire(RedisConstants.ALL_USER_LOOP_WASH, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                }
//                if (Objects.equals(false,isDelete)){
//                    redisTemplate.opsForList().leftPush(RedisConstants.ALL_USER_LOOP_WASH,wash.getId());
//                    redisTemplate.expire(RedisConstants.ALL_USER_LOOP_WASH, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//                }
//                return;
//            }
//
//            List<WashUser> washUserList = washUserService.find(wash.getId());
//            washUserList.forEach(washUser -> {
//                List<Long> export = redisTemplate.opsForList().range(RedisConstants.USER_LOOP_WASH+washUser.getUserId(),0,-1);
//                if (Objects.isNull(export)){
//                    export = new ArrayList<Long>();
//                }
//                export.remove(wash.getId());
//                redisTemplate.delete(RedisConstants.USER_LOOP_WASH+washUser.getUserId());
//                if (Objects.nonNull(export) && export.size()>0) {
//                    redisTemplate.opsForList().leftPushAll(RedisConstants.USER_LOOP_WASH + washUser.getUserId(), export);
//                    redisTemplate.expire(RedisConstants.USER_LOOP_WASH + washUser.getUserId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                }
//            });
//            if (Objects.nonNull(userIds) && userIds.size()>0) {
//                userIds.forEach(ui -> {
//                    redisTemplate.opsForList().leftPush(RedisConstants.USER_LOOP_WASH + ui, wash.getId());
//                    redisTemplate.expire(RedisConstants.USER_LOOP_WASH + ui, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//                });
//            }
//            return;
//        }
//
//
//        List<WashRegion> washRegionList = washRegionService.find(wash.getId());
//        washRegionList.forEach(washRegion -> {
//            List<Long> export = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+washRegion.getRegionId(),0,-1);
//            if (Objects.isNull(export)){
//                export = new ArrayList<Long>();
//            }
//            export.remove(washRegion.getWashId());
//            redisTemplate.delete(RedisConstants.REGION_WASH+washRegion.getRegionId());
//            if (Objects.nonNull(export) && export.size()>0) {
//                redisTemplate.opsForList().leftPushAll(RedisConstants.REGION_WASH + washRegion.getRegionId(), export);
//                redisTemplate.expire(RedisConstants.REGION_WASH + washRegion.getRegionId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            }
//        });
//
//        List<WashUser> washUserList = washUserService.find(wash.getId());
//        washUserList.forEach(washUser -> {
//            regionIds.forEach(ri->{
//                redisTemplate.delete(RedisConstants.REGION_USER_WASH+ri+washUser.getUserId());
//            });
//        });
//
//
//
//        redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//        regionIds.forEach(ri->{
//            redisTemplate.opsForList().leftPush(RedisConstants.REGION_WASH+ri,wash.getId());
//            redisTemplate.expire(RedisConstants.REGION_WASH+ri, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
//        });
//
//        userIds.forEach(ui->{
//            regionIds.forEach(ri-> {
//                redisTemplate.opsForValue().set(RedisConstants.REGION_USER_WASH +ri +ui, wash.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//            });
//        });
//    }
//}
