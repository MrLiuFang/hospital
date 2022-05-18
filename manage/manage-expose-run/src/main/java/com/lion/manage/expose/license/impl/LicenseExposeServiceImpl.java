package com.lion.manage.expose.license.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.license.LicenseDao;
import com.lion.manage.entity.license.License;
import com.lion.manage.expose.event.EventRecordExposeService;
import com.lion.manage.expose.license.LicenseExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(interfaceClass = LicenseExposeService.class)
public class LicenseExposeServiceImpl extends BaseServiceImpl<License> implements LicenseExposeService {

    @Autowired
    private LicenseDao licenseDao;


}
