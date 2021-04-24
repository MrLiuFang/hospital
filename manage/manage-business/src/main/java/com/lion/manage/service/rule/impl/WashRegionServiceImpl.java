package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.WashDao;
import com.lion.manage.dao.rule.WashRegionDao;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.rule.WashRegionService;
import com.lion.manage.service.rule.WashService;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:01
 */
@Service
public class WashRegionServiceImpl extends BaseServiceImpl<WashRegion> implements WashRegionService {

    @Autowired
    private WashRegionDao washRegionDao;

    @Autowired
    private RegionService regionService;

    @Override
    public void add(List<Long> regionId, Long washId) {
        if (Objects.nonNull(washId)){
            washRegionDao.deleteByWashId(washId);
        }
        regionId.forEach(id->{
            WashRegion washRegion = washRegionDao.findFirstByRegionId(id);
            if (Objects.nonNull(washRegion)){
                Region region = regionService.findById(washRegion.getRegionId());
                if (Objects.nonNull(region)){
                    BusinessException.throwException(region.getName()+"已存在洗手规则,一个区域设置多个洗手规则可能会冲突!");
                }
            }
            WashRegion newWashRegion = new WashRegion();
            newWashRegion.setRegionId(id);
            newWashRegion.setWashId(washId);
            save(newWashRegion);
        });
    }

    @Override
    public int delete(Long washId) {
        return washRegionDao.deleteByWashId(washId);
    }

    @Override
    public List<WashRegion> find(Long washId) {
        return washRegionDao.findByWashId(washId);
    }
}
