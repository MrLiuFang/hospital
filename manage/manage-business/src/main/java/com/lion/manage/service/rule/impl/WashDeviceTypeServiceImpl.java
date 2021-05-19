package com.lion.manage.service.rule.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashDeviceTypeDao;
import com.lion.manage.entity.rule.WashDeviceType;
import com.lion.manage.service.rule.WashDeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/14 上午10:02
 **/
@Service
public class WashDeviceTypeServiceImpl extends BaseServiceImpl<WashDeviceType> implements WashDeviceTypeService {

    @Autowired
    private WashDeviceTypeDao washDeviceTypeDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(Long washId, List<com.lion.manage.entity.enums.WashDeviceType> typeList) {
        if (Objects.nonNull(washId)) {
            washDeviceTypeDao.deleteByWashId(washId);
            redisTemplate.delete(RedisConstants.WASH_DEVICE_TYPE+washId);
            if (Objects.nonNull(typeList) && typeList.size()>0) {
                typeList.forEach(washDeviceType -> {
                    WashDeviceType type = new WashDeviceType();
                    if (Objects.nonNull(washDeviceType)) {
                        type.setType(washDeviceType);
                        type.setWashId(washId);
                        save(type);
                    }
                });
                redisTemplate.opsForList().leftPushAll(RedisConstants.WASH_DEVICE_TYPE+washId,typeList);
                redisTemplate.expire(RedisConstants.WASH_DEVICE_TYPE+washId,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            }
        }


    }

    @Override
    public List<com.lion.manage.entity.enums.WashDeviceType> find(Long washId) {
        List<WashDeviceType> list = washDeviceTypeDao.findByWashId(washId);
        List<com.lion.manage.entity.enums.WashDeviceType> returnList = new ArrayList<>();
        if (Objects.nonNull(list) && list.size()>0) {
            list.forEach(washDeviceType -> {
                returnList.add(washDeviceType.getType());
            });
        }
        return returnList;
    }
}
