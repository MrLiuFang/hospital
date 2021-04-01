package com.lion.manage.service.ward.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.ward.WardDao;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.service.ward.WardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:12
 */
@Service
public class WardServiceImpl extends BaseServiceImpl<Ward> implements WardService {

    @Autowired
    private WardDao wardDao;
}
