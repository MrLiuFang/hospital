package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.Tag;
import org.springframework.data.jpa.repository.Query;

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
     * @param battery
     * @return
     */
    public Integer countByDepartmentId(Long departmentId);

}
