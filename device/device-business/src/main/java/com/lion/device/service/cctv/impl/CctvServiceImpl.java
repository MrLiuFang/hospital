package com.lion.device.service.cctv.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.service.cctv.CctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:35
 */
@Service
public class CctvServiceImpl extends BaseServiceImpl<Cctv> implements CctvService {

    @Autowired
    private CctvDao cctvDao;
}
