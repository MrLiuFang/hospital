package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:15
 */
public interface TagDao extends BaseDao<Tag> {

    /**
     * 根据标签编码查询
     * @param tagCode
     * @return
     */
    Tag findFirstByTagCode(String tagCode);

    /**
     * 根据设备编码查询
     * @param deviceCode
     * @return
     */
    Tag findFirstByDeviceCode(String deviceCode);

    /**
     * 根据设备名称查询
     * @param deviceName
     * @return
     */
    Tag findFirstByDeviceName(String deviceName);

    /**
     * 根据资产id查询正在关联标签
     * @param assetsId
     * @return
     */
    @Query(" select t from Tag t join TagAssets ta on t.id = ta.tagId where ta.assetsId = :assetsId and ta.unbindingTime is null ")
    public Tag findByAssetsId(Long assetsId);

    /**
     * 根据科室统计电量标签
     * @param departmentId
     * @param battery
     * @return
     */
    public Integer countByDepartmentIdAndBattery(Long departmentId,Integer battery);

    /**
     * 根据科室内标签数量
     * @param departmentId
     * @return
     */
    public Integer countByDepartmentId(Long departmentId);

    /**
     * 根据科室查询标签
     * @param departmentId
     * @param purpose
     * @return
     */
    public List<Tag> findByDepartmentIdAndPurpose(Long departmentId,TagPurpose purpose);

    /**
     * 根据科室查询标签
     * @param departmentId
     * @param purpose
     * @param tagCode
     * @return
     */
    public List<Tag> findByDepartmentIdAndPurposeAndTagCodeLike(Long departmentId,TagPurpose purpose,String tagCode);

    /**
     * 根据科室内标签数量
     * @param departmentId
     * @param purpose
     * @return
     */
    public Integer countByDepartmentIdAndPurpose(Long departmentId, TagPurpose purpose);

    /**
     *
     * @param departmentId
     * @param purpose
     * @param deviceState
     * @return
     */
    public Integer countByDepartmentIdAndPurposeAndDeviceState(Long departmentId, TagPurpose purpose, State deviceState);

    @Query( " select t.id from Tag t ")
    public List<Long> allId();

    @Modifying
    @Transactional
    @Query(" update Tag set deviceState =:state ,version=version +1 where id = :id ")
    public void updateDeviceState(@Param("id")Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update Tag set state =:state ,version = version+1 where id = :id ")
    public void updateState(@Param("id")Long id, @Param("state") TagState state);

    @Modifying
    @Transactional
    @Query(" update Tag  set lastDataTime =:dateTime ,version = version+1 where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);

    @Query( " select t.id from Tag t where t.type = :tagType ")
    public List<Long> findId(@Param("tagType")TagType tagType);

    @Query( " select t.id from Tag t where t.type = :tagType and t.tagCode = :tagCode ")
    public List<Long> findId(@Param("tagType")TagType tagType,@Param("tagCode")String tagCode);

    @Query( " select t.id from Tag t where t.tagCode = :tagCode ")
    public List<Long> findId(@Param("tagCode")String tagCode);

    /**
     * 统计
     * @param purpose
     * @return
     */
    public int countByPurpose(TagPurpose purpose);
}
