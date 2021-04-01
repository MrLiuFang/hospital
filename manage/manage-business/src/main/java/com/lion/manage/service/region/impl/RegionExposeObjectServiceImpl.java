package com.lion.manage.service.region.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.region.RegionExposeObjectDao;
import com.lion.manage.entity.region.RegionExposeObject;
import com.lion.manage.service.region.RegionExposeObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:09
 */
@Service
public class RegionExposeObjectServiceImpl extends BaseServiceImpl<RegionExposeObject> implements RegionExposeObjectService {

    @Autowired
    private RegionExposeObjectDao regionExposeObjectDao;
}
