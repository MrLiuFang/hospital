package com.lion.event.service;

import com.lion.common.dto.UpdatePositionLeaveTimeDto;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.event.entity.Position;
import com.lion.event.entity.vo.ListPositionVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/1 下午6:12
 **/
public interface PositionService {

    public void save(Position position);

    /**
     * 获取员工指定时间内的行动轨迹
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<Position> findUserId(Long userId , LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 获取资产指定时间内的行动轨迹
     * @param assetsId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<Position> findByAssetsId(Long assetsId , LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 查询轨迹
     * @param pi
     * @param adi
     * @param ri
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<Position>> list(Long pi, Long adi,Long ri, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 病人/流动人员所到区域
     * @param personId
     * @param regionId
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public List<String> personAllRegion(Long personId,Long regionId, LocalDateTime startDateTime,LocalDateTime endDateTime );

    /**
     * 标签位置
     * @param tagPurpose
     * @param regionId
     * @param departmentId
     * @param deviceName
     * @param tagCode
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
     public IPageResultData<List<ListPositionVo>> tagPosition(TagPurpose tagPurpose, Long regionId,Long departmentId,String deviceName,String tagCode,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 更新患者离开区域时间
     * @param dto
     */
    public void updatePositionLeaveTime(UpdatePositionLeaveTimeDto dto);
}
