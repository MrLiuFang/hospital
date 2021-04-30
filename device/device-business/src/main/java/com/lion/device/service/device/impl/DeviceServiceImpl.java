package com.lion.device.service.device.impl;

import com.lion.common.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceService;
import com.lion.device.entity.device.Device;
import com.lion.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
    private DeviceGroupDeviceService deviceGroupDeviceService;

    @Autowired
    private RedisTemplate redisTemplate;

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
}
