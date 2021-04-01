package com.lion.device.expose.impl.cctv;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.expose.cctv.CctvExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:34
 */
@DubboService(interfaceClass = CctvExposeService.class)
public class CctvExposeServiceImpl extends BaseServiceImpl<Cctv> implements CctvExposeService {

    @Autowired
    private CctvDao cctvDao;


    @Override
    public List<Cctv> find(List<Long> ids) {
        return cctvDao.findByIdIn(ids);
    }
}
