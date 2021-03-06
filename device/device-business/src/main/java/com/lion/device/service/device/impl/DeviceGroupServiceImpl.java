package com.lion.device.service.device.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.dao.device.DeviceGroupDao;
import com.lion.device.dao.device.DeviceGroupDeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.dto.AddDeviceGroupDto;
import com.lion.device.entity.device.dto.UpdateDeviceGroupDto;
import com.lion.device.entity.device.vo.DetailsDeviceGroupVo;
import com.lion.device.entity.device.vo.ListDeviceGroupVo;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceGroupService;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:48
 */
@Service
public class DeviceGroupServiceImpl extends BaseServiceImpl<DeviceGroup> implements DeviceGroupService {

    @Autowired
    private DeviceGroupDao deviceGroupDao;

    @Autowired
    private DeviceGroupDeviceService deviceGroupDeviceService;

    @Autowired
    private DeviceGroupDeviceDao deviceGroupDeviceDao;

    @Autowired
    private DeviceDao deviceDao;

    @DubboReference
    private RegionExposeService regionExposeService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    @Transactional
    public void add(AddDeviceGroupDto addDeviceGroupDto) {
        DeviceGroup deviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(addDeviceGroupDto, deviceGroup);
        assertNameExist(deviceGroup.getName(),null);
        assertCodeExist(deviceGroup.getCode(),null);
        deviceGroup = save(deviceGroup);
        deviceGroupDeviceService.add(deviceGroup.getId(),addDeviceGroupDto.getDeviceIds());
//        persistenceRedis(addDeviceGroupDto.getDeviceIds(),Collections.EMPTY_LIST,deviceGroup.getId(),false);
    }

    @Override
    @Transactional
    public void update(UpdateDeviceGroupDto updateDeviceGroupDto) {
        DeviceGroup deviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(updateDeviceGroupDto, deviceGroup);
        assertNameExist(deviceGroup.getName(),deviceGroup.getId());
        assertCodeExist(deviceGroup.getCode(),deviceGroup.getId());
        List<DeviceGroupDevice> list = deviceGroupDeviceDao.findByDeviceGroupId(deviceGroup.getId());
        List<Long> oldDeviceIds = new ArrayList<Long>();
        list.forEach(deviceGroupDevice -> {
            oldDeviceIds.add(deviceGroupDevice.getDeviceId());
        });
        update(deviceGroup);
        deviceGroupDeviceService.add(deviceGroup.getId(),updateDeviceGroupDto.getDeviceIds());
//        persistenceRedis(updateDeviceGroupDto.getDeviceIds(),oldDeviceIds,deviceGroup.getId(),false);
    }

    @Override
    public IPageResultData<List<ListDeviceGroupVo>> list(String name, String code, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<DeviceGroup> page = findNavigator(lionPage);
        PageResultData<List<ListDeviceGroupVo>> pageResultData = new PageResultData<List<ListDeviceGroupVo>>(convertListDeviceGroupVo(page.getContent()),page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public DetailsDeviceGroupVo details(Long id) {
        com.lion.core.Optional<DeviceGroup> optional = findById(id);
        if (optional.isPresent()) {
            DeviceGroup deviceGroup = optional.get();
            DetailsDeviceGroupVo detailsDeviceGroupVo = new DetailsDeviceGroupVo();
            BeanUtils.copyProperties(deviceGroup, detailsDeviceGroupVo);
            List<Device> list = deviceDao.findByDeviceGroupId(id);
            List<DetailsDeviceGroupVo.DeviceVo> voList = new ArrayList<>();
            list.forEach(device -> {
                DetailsDeviceGroupVo.DeviceVo vo = new DetailsDeviceGroupVo.DeviceVo();
                BeanUtils.copyProperties(device,vo);
                vo.setDeviceGroupId(deviceGroup.getId());
                voList.add(vo);
            });
            detailsDeviceGroupVo.setDevices(voList);
            return detailsDeviceGroupVo;
        }
        return null;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(d->{
            deleteById(d.getId());
            List<DeviceGroupDevice> list = deviceGroupDeviceDao.findByDeviceGroupId(d.getId());
            List<Long> oldDeviceIds = new ArrayList<Long>();
            list.forEach(deviceGroupDevice -> {
                oldDeviceIds.add(deviceGroupDevice.getDeviceId());
            });
            deviceGroupDeviceService.deleteByDeviceGroupId(d.getId());
//            regionExposeService.deleteDeviceGroup(d.getId());
//            persistenceRedis(Collections.emptyList(),oldDeviceIds,d.getId(),true);
        });
    }

//    private void persistenceRedis(List<Long> newDeviceIds,List<Long> oldDeviceIds, Long deviceGroupId,Boolean isDelete){
//        Region region = regionExposeService.find(deviceGroupId);
//        if (Objects.nonNull(oldDeviceIds) && oldDeviceIds.size()>0){
//            oldDeviceIds.forEach(id->{
//                redisTemplate.delete(RedisConstants.DEVICE_REGION+id);
//            });
//        }
//        if (Objects.isNull(region)){
//            return;
//        }
//        if (Objects.equals(false,isDelete)){
//            if (Objects.nonNull(newDeviceIds) && newDeviceIds.size()>0){
//                newDeviceIds.forEach(id->{
//                    redisTemplate.opsForValue().set(RedisConstants.DEVICE_REGION+id,region.getId(),RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
//                });
//            }
//        }
//    }

    private void assertNameExist(String name, Long id) {
        DeviceGroup deviceGroup = deviceGroupDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(deviceGroup)) || (Objects.nonNull(id) && Objects.nonNull(deviceGroup) && !Objects.equals( deviceGroup.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000036"));
        }
    }

    private void assertCodeExist(String code, Long id) {
        DeviceGroup deviceGroup = deviceGroupDao.findFirstByCode(code);
        if ((Objects.isNull(id) && Objects.nonNull(deviceGroup)) || (Objects.nonNull(id) && Objects.nonNull(deviceGroup) && !Objects.equals(deviceGroup.getId() ,id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000037"));
        }
    }

    private List<ListDeviceGroupVo> convertListDeviceGroupVo(List<DeviceGroup> list){
        List<ListDeviceGroupVo> returnList = new ArrayList<ListDeviceGroupVo>();
        if (Objects.nonNull(list) && list.size()>0){
            list.forEach(deviceGroup -> {
                ListDeviceGroupVo listDeviceGroupVo = new ListDeviceGroupVo();
                BeanUtils.copyProperties(deviceGroup,listDeviceGroupVo);
                listDeviceGroupVo.setDeviceQuantity(deviceGroupDeviceDao.findByDeviceGroupId(deviceGroup.getId()).size());
                listDeviceGroupVo.setDevices(deviceDao.findByDeviceGroupId(deviceGroup.getId()));
                returnList.add(listDeviceGroupVo);
            });
        }
        return returnList;
    }

}
