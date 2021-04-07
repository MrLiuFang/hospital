package com.lion.manage.service.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.service.region.RegionCctvService;
import com.lion.manage.service.region.RegionService;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:11
 */
@Service
public class RegionCctvServiceImpl extends BaseServiceImpl<RegionCctv> implements RegionCctvService {

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Autowired
    private RegionService regionService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Override
    public void save(Long regionId, List<Long> cctvIds) {
        List<RegionCctv> listreRegionCctvs = regionCctvDao.findByRegionId(regionId);
        List<Long> oldCctvIds = new ArrayList<>();
        listreRegionCctvs.forEach(regionCctv -> {
            oldCctvIds.add(regionCctv.getCctvId());
        });
        if (Objects.nonNull(regionId)){
            regionCctvDao.deleteByRegionId(regionId);
        }else {
            return;
        }
        List<RegionCctv> list = new ArrayList<RegionCctv>();
        cctvIds.forEach(id->{
            RegionCctv regionCctv = new RegionCctv();
            regionCctv.setCctvId(id);
            regionCctv.setRegionId(regionId);
            list.add(regionCctv);
        });
        if (list.size()>0){
            saveAll(list);
        }
        Region region = regionService.findById(regionId);
        if (Objects.nonNull(region)){
            cctvExposeService.relationPosition(oldCctvIds,cctvIds,region.getBuildId(),region.getBuildFloorId(),regionId);
        }
    }

    @Override
    public List<RegionCctv> find(Long regionId) {
        return regionCctvDao.findByRegionId(regionId);
    }
}
