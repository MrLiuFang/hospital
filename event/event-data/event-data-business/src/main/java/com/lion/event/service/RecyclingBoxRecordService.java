package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.RecyclingBoxRecord;
import com.lion.event.entity.vo.ListRecyclingBoxCurrentVo;
import com.lion.event.entity.vo.ListRecyclingBoxRecordVo;

import java.io.IOException;
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
     * @param id
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListRecyclingBoxRecordVo>> list(Boolean isDisinfect,TagType tagType, String name, String code, String tagCode, LocalDateTime startDateTime, LocalDateTime endDateTime,Long id, LionPage lionPage);

    /**
     * 回收箱当前
     * @param startPreviousDisinfectDate
     * @param endPreviousDisinfectDate
     * @return
     */
    public IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentList(LocalDateTime startPreviousDisinfectDate,LocalDateTime endPreviousDisinfectDate, String name,String code, LionPage lionPage);


    IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentTagList(Long id);
    /**
     * 回收箱导出
     * @param startPreviousDisinfectDate
     * @param endPreviousDisinfectDate
     * @param name
     * @param code
     */
    public void recyclingBoxCurrentListExport(LocalDateTime startPreviousDisinfectDate,LocalDateTime endPreviousDisinfectDate, String name,String code) throws IOException, IllegalAccessException;


    /**
     * 一键消毒
     * @param recyclingBoxId
     */
    public void disinfect(Long recyclingBoxId);
}
