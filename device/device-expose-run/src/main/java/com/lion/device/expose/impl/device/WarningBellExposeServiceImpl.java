package com.lion.device.expose.impl.device;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.WarningBellDao;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.expose.device.WarningBellExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/9 上午10:43
 */
@DubboService(interfaceClass = WarningBellExposeService.class)
public class WarningBellExposeServiceImpl extends BaseServiceImpl<WarningBell> implements WarningBellExposeService {

    @Autowired
    private WarningBellDao warningBellDao;
}
