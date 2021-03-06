package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.WashRegionDao;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.rule.WashRegionService;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

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
                com.lion.core.Optional<Region> optional = regionService.findById(washRegion.getRegionId());
                if (optional.isPresent()){
                    BusinessException.throwException(optional.get().getName()+ MessageI18nUtil.getMessage("2000086"));
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
