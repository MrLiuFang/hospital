package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:05
 */
public interface TagExposeService extends BaseService<Tag> {

    /**
     * 根据资产id查询正在关联标签
     * @param assetsId
     * @return
     */
    public Tag find(Long assetsId);

    /**
     * 根据标签编码查询
     * @param tagCode
     * @return
     */
    public Tag find(String tagCode);

    public List<Tag> findByTagCode(String tagCode);

    /**
     * 更新电量
     * @param tagId
     * @param battery
     */
    public void updateBattery(Long tagId, Integer battery);

    /**
     * 根据科室统计电量标签
     * @param departmentId
     * @param battery
     * @return
     */
    public Integer countTag(Long departmentId,Integer battery);

    /**
     * 根据科室和标签编码查询
     *
     * @param departmentId
     * @param purpose
     * @param keyword
     * @param listIds
     * @return
     */
    public List<Tag> find(Long departmentId, TagPurpose purpose, String keyword, List<Long> listIds);


    /**
     * 根据科室统计电量标签
     * @param departmentId
     * @return
     */
    public Integer countTag(Long departmentId);


    /**
     * 根据科室统计标签
     *
     * @param departmentId
     * @param purpose
     * @param deviceState
     * @param isAlarm
     * @return
     */
    public Integer countTag(Long departmentId, TagPurpose purpose, State deviceState,Boolean isAlarm);

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateDeviceState(Long id, Integer state);

    public void updateIsAlarm(Long id,Boolean isAlarm);

    public void updateDeviceState(String code, State state);

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateState(Long id, Integer state);

    /**
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);

    public List<Long> allId();

    public List<Long> allId(TagPurpose tagPurpose);

    /**
     * 根据标签类型查询
     * @param tagType
     * @return
     */
    public List<Long> find(TagType tagType);

    /**
     * 查询id
     * @param tagType
     * @param tagCode
     * @return
     */
    public List<Long> find(TagType tagType,String tagCode);

//    public long countActive();

    public void updateRssi(String tagId, String rssi);

}
