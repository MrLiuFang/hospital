package com.lion.device.expose.impl.fault;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.fault.FaultDao;
import com.lion.device.entity.fault.Fault;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.device.expose.fault.FaultExposeService;
import com.lion.device.service.fault.FaultService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午11:03
 */
@DubboService(interfaceClass = FaultExposeService.class)
public class FaultExposeServiceImpl extends BaseServiceImpl<Fault> implements FaultExposeService {

    @Autowired
    private FaultService faultService;

    @Autowired
    private FaultDao faultDao;

    @Override
    public FaultDetailsVo findLast(Long relationId) {
        Fault fault = faultDao.findFirstByRelationIdOrderByCreateDateTimeDesc(relationId);
        if (Objects.nonNull(fault)) {
            FaultDetailsVo vo = new FaultDetailsVo();
            BeanUtils.copyProperties(fault,vo);
            vo = faultService.setInfoVo(vo);
            return vo;
        }
        return null;
    }

    @Override
    public int countFault(Long relationId) {
        return faultDao.countByRelationIdAndIsSolveIsFalse(relationId);
    }
}
