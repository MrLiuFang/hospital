package com.lion.person.expose.person.impl;
//
//import com.lion.core.service.impl.BaseServiceImpl;
//import com.lion.person.dao.person.TempLeaveDao;
//import com.lion.person.entity.person.TempLeave;
//import com.lion.person.expose.person.TempLeaveExposeService;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * @description:
// * @author: Mr.Liu
// * @time: 2021/6/2 上午11:45
// */
//@DubboService
//public class TempLeaveExposeServiceImpl extends BaseServiceImpl<TempLeave> implements TempLeaveExposeService {
//
//    @Autowired
//    private TempLeaveDao tempLeaveDao;
//
//    @Override
//    public List<TempLeave> find(Long patientId) {
//        return tempLeaveDao.findByPatientIdAndEndDateTimeAfterAndIsClosure(patientId, LocalDateTime.now(),false);
//    }
//
//
//}
