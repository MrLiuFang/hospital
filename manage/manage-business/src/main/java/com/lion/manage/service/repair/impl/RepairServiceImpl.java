package com.lion.manage.service.repair.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.repair.RepairDao;
import com.lion.manage.entity.repair.Repair;
import com.lion.manage.service.repair.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 下午3:00
 */
@Service
public class RepairServiceImpl extends BaseServiceImpl<Repair> implements RepairService {

    @Autowired
    private RepairDao repairDao;

}
