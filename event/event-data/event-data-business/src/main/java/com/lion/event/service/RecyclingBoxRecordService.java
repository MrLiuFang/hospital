package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.entity.vo.ListRecyclingBoxRecordVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:50
 */
public interface RecyclingBoxRecordService {

    public void save(RecyclingBoxRecord recyclingBoxRecord);

    /**
     * 列表
     *
     * @param isDisinfect
     * @param tagType
     * @param name
     * @param code
     * @param tagCode
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListRecyclingBoxRecordVo>> list(Boolean isDisinfect,TagType tagType, String name, String code, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 一键消毒
     */
    public void disinfect();
}
