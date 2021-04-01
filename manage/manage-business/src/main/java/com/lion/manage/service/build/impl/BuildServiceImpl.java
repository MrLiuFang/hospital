package com.lion.manage.service.build.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.build.BuildDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.service.build.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:05
 */
@Service
public class BuildServiceImpl extends BaseServiceImpl<Build> implements BuildService {

    @Autowired
    private BuildDao buildDao;
}
