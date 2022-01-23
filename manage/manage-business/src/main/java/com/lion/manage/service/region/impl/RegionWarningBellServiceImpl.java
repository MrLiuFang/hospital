package com.lion.manage.service.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.expose.device.WarningBellExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionWarningBellDao;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.service.region.RegionWarningBellService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:11
 */
@Service
public class RegionWarningBellServiceImpl extends BaseServiceImpl<RegionWarningBell> implements RegionWarningBellService {

    @Autowired
    private RegionWarningBellDao regionWarningBellDao;

    @DubboReference
    private WarningBellExposeService warningBellExposeService;

    @Override
    @Transactional
    public void add(List<Long> warningBellIds, Long regionId) {
        regionWarningBellDao.deleteByRegionId(regionId);
        if (Objects.isNull(warningBellIds) || warningBellIds.size()<=0) {
            return;
        }
        warningBellIds.forEach(id->{
            RegionWarningBell regionWarningBell = regionWarningBellDao.findFirstByWarningBellIdAndRegionIdNot(id,regionId);
            if (Objects.nonNull(regionWarningBell)) {
                com.lion.core.Optional<WarningBell> optional = warningBellExposeService.findById(regionWarningBell.getWarningBellId());
                if (optional.isPresent()) {
                    BusinessException.throwException(MessageI18nUtil.getMessage("2000117", new Object[]{optional.get().getName()}));
                }
            }
        });
        warningBellIds.forEach(id->{
            RegionWarningBell warningBell = new RegionWarningBell();
            warningBell.setWarningBellId(id);
            warningBell.setRegionId(regionId);
            save(warningBell);
        });
    }

    @Override
    public List<RegionWarningBell> find(Long regionId) {
        return regionWarningBellDao.findByRegionId(regionId);
    }
}
