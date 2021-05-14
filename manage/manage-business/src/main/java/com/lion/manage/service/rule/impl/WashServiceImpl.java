package com.lion.manage.service.rule.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.WashDao;
import com.lion.manage.dao.rule.WashRegionDao;
import com.lion.manage.dao.rule.WashUserDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.entity.rule.WashUser;
import com.lion.manage.entity.rule.dto.AddWashDto;
import com.lion.manage.entity.rule.dto.UpdateWashDto;
import com.lion.manage.entity.rule.vo.DetailsWashVo;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.department.DepartmentUserService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.rule.*;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:55
 */
@Service
public class WashServiceImpl extends BaseServiceImpl<Wash> implements WashService {

    @Autowired
    private WashDao washDao;

    @Autowired
    private WashRegionDao washRegionDao;

    @Autowired
    private WashUserDao washUserDao;

    @Autowired
    private WashDeviceService washDeviceService;

    @Autowired
    private WashRegionService washRegionService;

    @Autowired
    private WashUserServcie washUserService;

    @Autowired
    private RegionService regionService;


    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private DepartmentService departmentService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private WashDeviceTypeService washDeviceTypeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void add(AddWashDto addWashDto) {
        Wash wash = new Wash();
        BeanUtils.copyProperties(addWashDto,wash);
        assertNameExist(wash.getName(),null);
        assertEnteringTime(wash,false);
        assertLoopWashExist(wash.getIsAllUser(),wash.getId());
        wash = save(wash);
        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
            washRegionService.add(addWashDto.getRegionId(),wash.getId());
            washDeviceService.add(addWashDto.getDeviceId(),wash.getId());
        }
        if (Objects.equals(wash.getIsAllUser(),false)){
            washUserService.add(addWashDto.getUserId(),wash.getId());
        }
        if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.nonNull(addWashDto.getDeviceTypes()) && addWashDto.getDeviceTypes().size()>0){
            washDeviceTypeService.add(wash.getId(),addWashDto.getDeviceTypes());
        }
        persistence2Redis(addWashDto.getRegionId(),addWashDto.getUserId(),addWashDto.getDeviceId(),wash,false);
    }

    @Override
    @Transactional
    public void update(UpdateWashDto updateWashDto) {
        Wash wash = new Wash();
        BeanUtils.copyProperties(updateWashDto,wash);
        assertNameExist(wash.getName(),wash.getId());
        assertEnteringTime(wash,true);
        assertLoopWashExist(wash.getIsAllUser(),wash.getId());
        update(wash);
        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
            washRegionService.add(updateWashDto.getRegionId(),wash.getId());
            washDeviceService.add(updateWashDto.getDeviceId(),wash.getId());
        }
        if (Objects.equals(wash.getIsAllUser(),false)){
            washUserService.add(updateWashDto.getUserId(),wash.getId());
        }
        if (Objects.equals(wash.getType(), WashRuleType.LOOP) && Objects.nonNull(updateWashDto.getDeviceTypes()) && updateWashDto.getDeviceTypes().size()>0){
            washDeviceTypeService.add(wash.getId(),updateWashDto.getDeviceTypes());
        }

        persistence2Redis(updateWashDto.getRegionId(),updateWashDto.getUserId(),updateWashDto.getDeviceId(),wash,false);
    }

    @Override
    public DetailsWashVo details(Long id) {
        Wash wash = this.findById(id);
        if (Objects.isNull(wash)){
            return null;
        }
        DetailsWashVo detailsWashVo = new DetailsWashVo();
        BeanUtils.copyProperties(wash,detailsWashVo);
        List<WashDevice> washDeviceList = washDeviceService.find(wash.getId());
        if (Objects.nonNull(washDeviceList) && washDeviceList.size()>0){
            List<DetailsWashVo.DeviceVo> vos = new ArrayList<DetailsWashVo.DeviceVo>();
            washDeviceList.forEach(washDevice->{
                Device device = deviceExposeService.findById(washDevice.getDeviceId());
                if (Objects.nonNull(device)){
                    DetailsWashVo.DeviceVo vo = new DetailsWashVo.DeviceVo();
                    BeanUtils.copyProperties(device,vo);
                    vos.add(vo);
                }
            });
            detailsWashVo.setDeviceVos(vos);
        }
        List<WashRegion>  washRegions = washRegionService.find(wash.getId());
        if (Objects.nonNull(washRegions) && washRegions.size()>0){
            List<DetailsWashVo.RegionVo> regionVos = new ArrayList<DetailsWashVo.RegionVo>();
            washRegions.forEach(washRegion->{
                Region region = regionService.findById(washRegion.getRegionId());
                if (Objects.nonNull(region)){
                    DetailsWashVo.RegionVo regionVo = new DetailsWashVo.RegionVo();
                    regionVo.setRegionName(region.getName());
                    regionVo.setRemarks(region.getRemarks());
                    regionVo.setId(region.getId());
                    Build build = buildService.findById(region.getBuildId());
                    if (Objects.nonNull(build)){
                        regionVo.setBuildName(build.getName());
                    }
                    BuildFloor buildFloor = buildFloorService.findById(region.getBuildFloorId());
                    if (Objects.nonNull(buildFloor)){
                        regionVo.setBuildFloorName(buildFloor.getName());
                    }
                    regionVos.add(regionVo);
                }
            });
            detailsWashVo.setRegionVos(regionVos);
        }
        List<WashUser> washUsers = washUserService.find(wash.getId());
        if (Objects.nonNull(washUsers) &&  washUsers.size()>0){
            List<DetailsWashVo.UserVo> userVos = new ArrayList<>();
            washUsers.forEach(washUser -> {
                User user = userExposeService.findById(washUser.getUserId());
                if (Objects.nonNull(user)){
                    DetailsWashVo.UserVo userVo = new DetailsWashVo.UserVo();
                    userVo.setId(user.getId());
                    Department department = departmentUserService.findDepartment(user.getId());
                    if (Objects.nonNull(department)){
                        userVo.setDepartmentName(department.getName());
                    }
                    userVo.setName(user.getName());
                    userVo.setNumber(user.getNumber());
                    userVo.setTagCode(user.getTagCode());
                    userVo.setUserType(user.getUserType());
                    userVos.add(userVo);
                }
            });
            detailsWashVo.setUserVos(userVos);
        }
        return detailsWashVo;
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtos) {
        deleteDtos.forEach(deleteDto -> {
            persistence2Redis(Collections.EMPTY_LIST,Collections.EMPTY_LIST,Collections.EMPTY_LIST,this.findById(deleteDto.getId()),true);
            this.deleteById(deleteDto.getId());
            washDeviceService.delete(deleteDto.getId());
            washRegionService.delete(deleteDto.getId());
            washUserService.delete(deleteDto.getId());
            washDeviceTypeService.add(deleteDto.getId(),null);
        });
    }

    private void assertLoopWashExist(Boolean isAllUser, Long id) {
        List<WashUser> washUserList = washUserDao.find(WashRuleType.LOOP,false);
        if (Objects.nonNull(washUserList) && washUserList.size()>0) {
            if (Objects.equals(true,isAllUser)) {
                BusinessException.throwException("已有其他人员在非全员的定时洗手规则中,不能再设置针对全员的定时洗手规则,会造成洗手监控冲突");
            }
        }

        Wash washLoppAllUser = washDao.findFirstByTypeAndIsAllUser(WashRuleType.LOOP, true);
        if ((Objects.isNull(id) && Objects.nonNull(washLoppAllUser) ) || (Objects.nonNull(id) && Objects.nonNull(washLoppAllUser) &&  !Objects.equals(washLoppAllUser.getId(),id)) ){
            BusinessException.throwException("针对全员的定时洗手规则已经存在,多个定时洗手规则会造成洗手监控冲突");
        }

        if (Objects.nonNull(washLoppAllUser) && Objects.equals(false,isAllUser)){
            BusinessException.throwException("针对全员的定时洗手规则已经存在,不能再给员工单独设置洗手规则");
        }

    }

    private void assertEnteringTime(Wash wash,Boolean isUpdate) {
        if (Objects.nonNull(wash) &&Objects.equals(wash.getType(),WashRuleType.LOOP)){
            return;
        }
        if (Objects.nonNull(wash.getAfterEnteringTime()) && Objects.nonNull(wash.getBeforeEnteringTime())) {
            BusinessException.throwException("检测洗手时间（进入之前/进入之后）只能二选一");
        }
        if (Objects.isNull(wash.getAfterEnteringTime()) && Objects.isNull(wash.getBeforeEnteringTime())) {
            BusinessException.throwException("检测洗手时间（进入之前/进入之后）必须选一个");
        }
        if (Objects.nonNull(wash.getAfterEnteringTime())) {
            if (wash.getAfterEnteringTime()<=0){
                BusinessException.throwException("检测时间必须大于0分钟");
            }
            wash.setBeforeEnteringTime(null);
            if (Objects.equals(true,isUpdate)) {
                washDao.setBeforeEnteringTime(wash.getId());
            }
        }else if (Objects.nonNull(wash.getBeforeEnteringTime())) {
            if (wash.getBeforeEnteringTime()<=0){
                BusinessException.throwException("检测时间必须大于0分钟");
            }
            wash.setAfterEnteringTime(null);
            if (Objects.equals(true,isUpdate)) {
                washDao.setAfterEnteringTimeIsNull(wash.getId());
            }
        }
    }

    private void assertNameExist(String name, Long id) {
        Wash wash = washDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(wash) ) || (Objects.nonNull(id) && Objects.nonNull(wash) &&  !Objects.equals(wash.getId(),id)) ){
            BusinessException.throwException("该名称已存在");
        }
    }

    private void persistence2Redis(List<Long> regionIds,List<Long> userIds,List<Long> deviceIds,Wash wash,Boolean isDelete){
        redisTemplate.delete(RedisConstants.WASH_DEVICE+wash.getId());
        if (Objects.equals(false,isDelete)){
            if (Objects.nonNull(deviceIds) && deviceIds.size()>0) {
                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE + wash.getId(), deviceIds);
                redisTemplate.expire(RedisConstants.WASH_DEVICE + wash.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }

        if (Objects.equals(wash.getType(),WashRuleType.LOOP)) {
            if (wash.getIsAllUser()){
                List<Long> list = redisTemplate.opsForList().range(RedisConstants.ALL_USER_LOOP_WASH,0,-1);
                if (Objects.isNull(list)){
                    list = new ArrayList<Long>();
                }
                list.remove(wash.getId());
                redisTemplate.delete(RedisConstants.ALL_USER_LOOP_WASH);
                if (Objects.nonNull(list) && list.size()>0) {
                    redisTemplate.opsForList().leftPushAll(RedisConstants.ALL_USER_LOOP_WASH, list);
                    redisTemplate.expire(RedisConstants.ALL_USER_LOOP_WASH, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
                if (Objects.equals(false,isDelete)){
                    redisTemplate.opsForList().leftPush(RedisConstants.ALL_USER_LOOP_WASH,wash.getId());
                    redisTemplate.expire(RedisConstants.ALL_USER_LOOP_WASH, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
                }
                return;
            }

            List<WashUser> washUserList = washUserService.find(wash.getId());
            washUserList.forEach(washUser -> {
                List<Long> list = redisTemplate.opsForList().range(RedisConstants.USER_LOOP_WASH+washUser.getUserId(),0,-1);
                if (Objects.isNull(list)){
                    list = new ArrayList<Long>();
                }
                list.remove(wash.getId());
                redisTemplate.delete(RedisConstants.USER_LOOP_WASH+washUser.getUserId());
                if (Objects.nonNull(list) && list.size()>0) {
                    redisTemplate.opsForList().leftPushAll(RedisConstants.USER_LOOP_WASH + washUser.getUserId(), list);
                    redisTemplate.expire(RedisConstants.USER_LOOP_WASH + washUser.getUserId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                }
            });
            if (Objects.nonNull(userIds) && userIds.size()>0) {
                userIds.forEach(ui -> {
                    redisTemplate.opsForList().leftPush(RedisConstants.USER_LOOP_WASH + ui, wash.getId());
                    redisTemplate.expire(RedisConstants.USER_LOOP_WASH + ui, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
                });
            }
            return;
        }


        List<WashRegion> washRegionList = washRegionService.find(wash.getId());
        washRegionList.forEach(washRegion -> {
            List<Long> list = redisTemplate.opsForList().range(RedisConstants.REGION_WASH+washRegion.getRegionId(),0,-1);
            if (Objects.isNull(list)){
                list = new ArrayList<Long>();
            }
            list.remove(washRegion.getWashId());
            redisTemplate.delete(RedisConstants.REGION_WASH+washRegion.getRegionId());
            if (Objects.nonNull(list) && list.size()>0) {
                redisTemplate.opsForList().leftPushAll(RedisConstants.REGION_WASH + washRegion.getRegionId(), list);
                redisTemplate.expire(RedisConstants.REGION_WASH + washRegion.getRegionId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        });

        List<WashUser> washUserList = washUserService.find(wash.getId());
        washUserList.forEach(washUser -> {
            regionIds.forEach(ri->{
                redisTemplate.delete(RedisConstants.REGION_USER_WASH+ri+washUser.getUserId());
            });
        });



        redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        regionIds.forEach(ri->{
            redisTemplate.opsForList().leftPush(RedisConstants.REGION_WASH+ri,wash.getId());
            redisTemplate.expire(RedisConstants.REGION_WASH+ri, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
        });

        userIds.forEach(ui->{
            regionIds.forEach(ri-> {
                redisTemplate.opsForValue().set(RedisConstants.REGION_USER_WASH +ri +ui, wash.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            });
        });
    }
}
