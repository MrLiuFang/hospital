package com.lion.device.dao.device;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:32
 */
public interface DeviceDao extends BaseDao<Device> {

    /**
     * 根据名称查询设备
     * @param name
     * @return
     */
    public Device findFirstByName(String name);

    /**
     * 根据编号查询设备
     * @param code
     * @return
     */
    public Device findFirstByCode(String code);

    /**
     * 查询设备组所有的设备
     * @param deviceGroupId
     * @return
     */
    @Query(" select d from Device d join DeviceGroupDevice dgd on d.id = dgd.deviceId where dgd.deviceGroupId = :deviceGroupId ")
    public List<Device> findByDeviceGroupId(Long deviceGroupId);

    /**
     * 根据大类统计数量
     * @param classify
     * @return
     */
    public Integer countByDeviceClassify(DeviceClassify classify);
}
