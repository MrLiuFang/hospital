package com.lion.device.expose.fault;

import com.lion.core.service.BaseService;
import com.lion.device.entity.fault.Fault;
import com.lion.device.entity.fault.vo.FaultDetailsVo;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午11:02
 */
public interface FaultExposeService extends BaseService<Fault> {

    public FaultDetailsVo findLast(Long relationId);

    /**
     * 统计未处理的故障
     * @param relationId
     * @return
     */
    public int countFault(Long relationId);
}
