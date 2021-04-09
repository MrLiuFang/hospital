package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashRegionDao;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.service.rule.WashRegionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void add(List<Long> regionId, Long washId) {
        if (Objects.nonNull(washId)){
            washRegionDao.deleteByWashId(washId);
        }
        regionId.forEach(id->{
            WashRegion washRegion = new WashRegion();
            washRegion.setRegionId(id);
            washRegion.setWashId(washId);
            save(washRegion);
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
