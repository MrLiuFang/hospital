package com.lion.device.dao.device;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.enums.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:43
 */
public interface DeviceGroupDeviceDao extends BaseDao<DeviceGroupDevice> {

    /**
     * 根据设备ID删除
     * @param deviceId
     * @return
     */
    public int deleteByDeviceId(Long deviceId);

    /**
     * 根据设备组ID删除
     * @param deviceGroupId
     * @return
     */
    public int deleteByDeviceGroupId(Long deviceGroupId);

    /**
     * 根据设备组ID查询关联的设备ID
     * @param deviceGroupId
     * @return
     */
    public List<DeviceGroupDevice> findByDeviceGroupId(Long deviceGroupId);

    /**
     * 根据设备ID查询
     * @param deviceId
     * @return
     */
    public DeviceGroupDevice findFirstByDeviceId(Long deviceId);

    /**
     * 根据设备组统计设备
     * @param deviceGroupId
     * @return
     */
    public int countByDeviceGroupId(Long deviceGroupId);

    /**
     * 统计
     * @param deviceGroupId
     * @param state
     * @return
     */
    @Query(" select count(dgd) from DeviceGroupDevice dgd join  Device d on d.id = dgd.deviceId where dgd.deviceGroupId = :deviceGroupId and d.deviceState=:state " )
    public int count(@Param("deviceGroupId") Long deviceGroupId,@Param("state") State state);

}
