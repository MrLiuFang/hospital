package com.lion.manage.service.build.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.build.BuildFloorDao;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.service.build.BuildFloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:06
 */
@Service
public class BuildFloorServiceImpl extends BaseServiceImpl<BuildFloor> implements BuildFloorService {

    @Autowired
    private BuildFloorDao  buildFloorDao;
}
