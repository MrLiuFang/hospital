package com.lion.manage.expose.event;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.event.EventRecord;
import com.lion.manage.entity.event.vo.EventRecordVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/29 上午10:12
 */
public interface EventRecordExposeService extends BaseService<EventRecord> {

    /**
     * 记录事件
     * @param code
     * @param remarks
     * @param content
     * @param searchCriteria
     * @param url
     * @return
     */
    public EventRecord add(String code,String remarks,String content,String searchCriteria,String url);

    /**
     * 列表
     * @param startDatetime
     * @param endDateTime
     * @param code
     * @param name
     * @param lionPage
     * @return
     */
    public IPageResultData<List<EventRecordVo>> list(LocalDateTime startDatetime, LocalDateTime endDateTime, String code, String name, LionPage lionPage);

    /**
     * 详情
     * @param id
     * @return
     */
    public EventRecordVo details(Long id);
}
