package com.lion.manage.service.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.entity.region.RegionCctv;
import com.lion.manage.service.region.RegionCctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:11
 */
@Service
public class RegionCctvServiceImpl extends BaseServiceImpl<RegionCctv> implements RegionCctvService {

    @Autowired
    private RegionCctvDao regionCctvDao;
}
