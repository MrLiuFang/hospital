package com.lion.event.service;

import com.lion.common.dto.UserLastWashDto;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.WashRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 上午8:46
 **/
public interface WashRecordService {

    /**
     * 保存
     * @param washRecord
     */
    public void save(WashRecord washRecord);

    /**
     * 列表
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<WashRecord>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 更新洗手时长
     * @param userLastWashDto
     */
    public void updateWashTime(UserLastWashDto userLastWashDto);
}
