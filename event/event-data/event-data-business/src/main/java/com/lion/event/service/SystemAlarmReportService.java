package com.lion.event.service;

import com.lion.core.service.BaseService;
import com.lion.event.entity.SystemAlarmReport;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.vo.SystemAlarmReportDetailsVo;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/28 下午1:56
 */
public interface SystemAlarmReportService  {

    public void alarmReport(AlarmReportDto alarmReportDto);

    /**
     * 查询警告汇报
     * @param systemAlarmId
     * @return
     */
    public List<SystemAlarmReportDetailsVo> list(String systemAlarmId);

}
