package com.lion.manage.service.rule.impl;

import com.lion.common.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.WashDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.WashDeviceType;
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
import com.lion.manage.service.rule.WashDeviceService;
import com.lion.manage.service.rule.WashRegionService;
import com.lion.manage.service.rule.WashService;
import com.lion.manage.service.rule.WashUserServcie;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
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
 * @date 2021/4/9下午4:55
 */
@Service
public class WashServiceImpl extends BaseServiceImpl<Wash> implements WashService {

    @Autowired
    private WashDao washDao;

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

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentUserService departmentUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void add(AddWashDto addWashDto) {
        Wash wash = new Wash();
        BeanUtils.copyProperties(addWashDto,wash);
        assertNameExist(wash.getName(),null);
        wash = save(wash);
        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
            washRegionService.add(addWashDto.getRegionId(),wash.getId());
        }
        if (Objects.equals(wash.getIsAllUser(),false)){
            washUserService.add(addWashDto.getUserId(),wash.getId());
        }
        washDeviceService.add(addWashDto.getDeviceType(),wash.getId());
        persistence2Redis(addWashDto.getRegionId(),addWashDto.getUserId(),wash,false);
    }

    @Override
    @Transactional
    public void update(UpdateWashDto updateWashDto) {
        Wash wash = new Wash();
        BeanUtils.copyProperties(updateWashDto,wash);
        assertNameExist(wash.getName(),wash.getId());
        update(wash);
        if (Objects.equals(wash.getType(), WashRuleType.REGION)){
            washRegionService.add(updateWashDto.getRegionId(),wash.getId());
        }
        if (Objects.equals(wash.getIsAllUser(),false)){
            washUserService.add(updateWashDto.getUserId(),wash.getId());
        }
        washDeviceService.add(updateWashDto.getDeviceType(),wash.getId());
        persistence2Redis(updateWashDto.getRegionId(),updateWashDto.getUserId(),wash,false);
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
            List<WashDeviceType> deviceTypes = new ArrayList<WashDeviceType>();
            washDeviceList.forEach(type->{
                deviceTypes.add(type.getType());
            });
            detailsWashVo.setDeviceType(deviceTypes);
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
            persistence2Redis(Collections.EMPTY_LIST,Collections.EMPTY_LIST,this.findById(deleteDto.getId()),true);
            this.deleteById(deleteDto.getId());
            washDeviceService.delete(deleteDto.getId());
            washRegionService.delete(deleteDto.getId());
            washUserService.delete(deleteDto.getId());
        });
    }

    private void assertNameExist(String name, Long id) {
        Wash wash = washDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(wash) ){
            BusinessException.throwException("该名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(wash) && !wash.getId().equals(id)){
            BusinessException.throwException("该名称已存在");
        }
    }

    private void persistence2Redis(List<Long> regionId,List<Long> userId,Wash wash,Boolean isDelete){
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
            if (Objects.nonNull(userId) && userId.size()>0) {
                userId.forEach(ui -> {
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
            regionId.forEach(ri->{
                redisTemplate.delete(RedisConstants.REGION_USER_WASH+ri+washUser.getUserId());
            });
        });

        redisTemplate.opsForValue().set(RedisConstants.WASH+wash.getId(),wash, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        regionId.forEach(ri->{
            redisTemplate.opsForList().leftPush(RedisConstants.REGION_WASH+ri,wash.getId());
            redisTemplate.expire(RedisConstants.REGION_WASH+ri, RedisConstants.EXPIRE_TIME,TimeUnit.DAYS);
        });

        userId.forEach(ui->{
            regionId.forEach(ri-> {
                redisTemplate.opsForValue().set(RedisConstants.REGION_USER_WASH +ri +ui, wash.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            });
        });
    }
}
