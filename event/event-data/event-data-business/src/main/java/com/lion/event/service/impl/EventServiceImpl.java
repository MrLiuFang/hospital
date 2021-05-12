package com.lion.event.service.impl;

import com.lion.event.dao.EventDao;
import com.lion.event.entity.Event;
import com.lion.event.entity.vo.WashMonitorVo;
import com.lion.event.service.EventService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/1 下午6:11
 **/
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDao eventDao;

    @Override
    public void save(Event event) {
        eventDao.save(event);
    }

    @Override
    public void updateUadt(String uuid, LocalDateTime uadt ) {
        eventDao.updateUadt(uuid,uadt);
    }

    @Override
    public WashMonitorVo eventCount(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        WashMonitorVo washMonitorVo = new WashMonitorVo();
        //医院所有事件
        List<Document> listAll = eventDao.eventCount(startDateTime,endDateTime,false,null,null);
        Integer all = listAll.size()>0?listAll.get(0).getInteger("count",0):0;
        //医院所有合规事件
        List<Document> listAllNoAlarm = eventDao.eventCount(startDateTime,endDateTime,false,false,null);
        Integer allNoAlarm = listAllNoAlarm.size()>0?listAllNoAlarm.get(0).getInteger("count",0):0;
        //医院所有违规事件
        List<Document> listAllViolation = eventDao.eventCount(startDateTime,endDateTime,false,true,false);
        Integer allViolation = listAllViolation.size()>0?listAllViolation.get(0).getInteger("count",0):0;
        //医院所有错过洗手事件
        List<Document> listAllNoWash = eventDao.eventCount(startDateTime,endDateTime,false,true,true);
        Integer allNoWash = listAllNoWash.size()>0?listAllNoWash.get(0).getInteger("count",0):0;

        WashMonitorVo.Ratio ratio = new WashMonitorVo.Ratio();
        ratio.setName("全院合规率");
        BigDecimal bigDecimalAll = new BigDecimal(all);
        if (new BigDecimal(allViolation).compareTo(new BigDecimal(0)) != 0) {
            ratio.setViolation(new BigDecimal(allViolation).divide(bigDecimalAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
        if (new BigDecimal(allNoAlarm).compareTo(new BigDecimal(0)) != 0) {
            ratio.setConformance(new BigDecimal(allNoAlarm).divide(bigDecimalAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
        if (new BigDecimal(allNoWash).compareTo(new BigDecimal(0)) != 0) {
            ratio.setNoWash(new BigDecimal(allNoWash).divide(bigDecimalAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
        }
        washMonitorVo.setHospital(ratio);

        //所有科室事件
        List<Document> listDepartmentAll = eventDao.eventCount(startDateTime,endDateTime,true,null,null);

        //医院科室合规事件
        List<Document> listDepartmentAllNoAlarm = eventDao.eventCount(startDateTime,endDateTime,true,false,null);

        //医院科室违规事件
        List<Document> listDepartmentAllViolation = eventDao.eventCount(startDateTime,endDateTime,true,true,false);

        //医院科室错过洗手事件
        List<Document> listDepartmentAllNoWash = eventDao.eventCount(startDateTime,endDateTime,true,true,true);

        Map<String,WashMonitorVo.Ratio> map = new HashMap<>();
        List<WashMonitorVo.Ratio> list = new ArrayList<>();
        listDepartmentAll.forEach(document -> {
            WashMonitorVo.Ratio r = new WashMonitorVo.Ratio();
            r.setName(document.getString("_id"));
            BigDecimal departmentAll = new BigDecimal(document.getInteger("count"));
            for (Document d : listDepartmentAllNoAlarm){
                if (Objects.equals(r.getName(), d.getString("_id"))){
                    Integer departmentAllNoAlarm = d.getInteger("count");
                    if (new BigDecimal(departmentAllNoAlarm).compareTo(new BigDecimal(0)) != 0) {
                        r.setConformance(new BigDecimal(departmentAllNoAlarm).divide(departmentAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                        break;
                    }
                }
            }
            for (Document d : listDepartmentAllViolation){
                if (Objects.equals(r.getName(), d.getString("_id"))){
                    Integer departmentAllViolation = d.getInteger("count");
                    if (new BigDecimal(departmentAllViolation).compareTo(new BigDecimal(0)) != 0) {
                        r.setViolation(new BigDecimal(departmentAllViolation).divide(departmentAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                        break;
                    }
                }
            }
            for (Document d : listDepartmentAllNoWash){
                if (Objects.equals(r.getName(), d.getString("_id"))){
                    Integer departmentAllNoWash = d.getInteger("count");
                    if (new BigDecimal(departmentAllNoWash).compareTo(new BigDecimal(0)) != 0) {
                        r.setNoWash(new BigDecimal(departmentAllNoWash).divide(departmentAll,4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                        break;
                    }
                }
            }

            list.add(r);
        });
        washMonitorVo.setDepartment(list);
        return washMonitorVo;
    }
}
