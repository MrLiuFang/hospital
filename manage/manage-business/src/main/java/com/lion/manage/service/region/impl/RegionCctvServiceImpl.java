package com.lion.manage.service.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.service.region.RegionCctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:11
 */
@Service
public class RegionCctvServiceImpl extends BaseServiceImpl<RegionCctv> implements RegionCctvService {

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Override
    public void save(Long regionId, List<Long> cctvIds) {
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
    }

    @Override
    public List<RegionCctv> find(Long regionId) {
        return regionCctvDao.findByRegionId(regionId);
    }
}
