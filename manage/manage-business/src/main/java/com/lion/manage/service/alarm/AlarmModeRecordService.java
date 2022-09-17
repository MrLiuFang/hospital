package com.lion.manage.service.alarm;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.alarm.AlarmModeRecord;
import com.lion.manage.entity.alarm.vo.ListAlarmModeRecordVo;
import com.lion.upms.entity.enums.AlarmMode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:08
 */
public interface AlarmModeRecordService extends BaseService<AlarmModeRecord> {

    /**
     * 列表
     * @param startDateTime
     * @param endDateTime
     * @param alarmMode
     * @param name
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListAlarmModeRecordVo>> list(LocalDateTime startDateTime, LocalDateTime endDateTime, AlarmMode alarmMode, String name, LionPage lionPage);

    /**
     * 导出
     *
     * @param startDateTime
     * @param endDateTime
     * @param alarmMode
     * @param name
     * @param lionPage
     */
    void export(LocalDateTime startDateTime, LocalDateTime endDateTime, AlarmMode alarmMode, String name,LionPage lionPage) throws IOException, IllegalAccessException;
}
