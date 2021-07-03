package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.entity.vo.ListHumitureRecordVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/17 下午9:15
 **/
public interface HumitureRecordService {

    public void save(HumitureRecord humitureRecord);

    /**
     *  温湿标签列表
     * @param regionId
     * @param departmentId
     * @param deviceCode
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListHumitureRecordVo>> temperatureHumidityList(Long regionId, Long departmentId, String deviceCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 查询最后的记录
     * @param tagId
     * @return
     */
    public HumitureRecord findLast(Long tagId);
}
