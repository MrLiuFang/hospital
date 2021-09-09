package com.lion.manage.expose.region.impl;
//
//import com.lion.core.service.impl.BaseServiceImpl;
//import com.lion.manage.dao.region.RegionDeviceDao;
//import com.lion.manage.entity.region.RegionDevice;
//import com.lion.manage.expose.region.RegionDeviceExposeService;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * @author Mr.Liu
// * @description $
// * @createDateTime 2021/9/8 下午2:49
// */
//@DubboService(interfaceClass = RegionDeviceExposeService.class)
//public class RegionDeviceExposeServiceImpl extends BaseServiceImpl<RegionDevice> implements RegionDeviceExposeService {
//
//    @Autowired
//    private RegionDeviceDao regionDeviceDao;
//
//
//    @Override
//    public RegionDevice find(Long deviceId) {
//        return regionDeviceDao.findFirstByDeviceId(deviceId);
//    }
//}
