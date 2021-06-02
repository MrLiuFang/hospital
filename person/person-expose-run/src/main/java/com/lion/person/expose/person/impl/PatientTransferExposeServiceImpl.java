package com.lion.person.expose.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.expose.person.PatientTransferExposeService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 下午12:01
 */
@DubboService
public class PatientTransferExposeServiceImpl extends BaseServiceImpl<PatientTransfer> implements PatientTransferExposeService {
}
