package com.lion.device.dao.device;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:32
 */
public interface DeviceDao extends BaseDao<Device>,DeviceDaoEx {

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

    /**
     * 根据设备组统计电量设备
     * @param deviceGroupIds
     * @param battery
     * @return
     */
    @Query(" select count(d) from Device d join DeviceGroupDevice dgd on d.id = dgd.deviceId where dgd.deviceGroupId in :deviceGroupIds and d.battery = :battery ")
    public Integer countDevice(List<Long> deviceGroupIds, Integer battery);


    @Query( " select d.id from Device d ")
    public List<Long> allId();

    /**
     * 设备统计
     * @param buildId
     * @param buildFloorId
     * @param deviceState
     * @param lionPage
     * @return
     */
    public Page<Device> findByBuildIdAndBuildFloorIdAndDeviceState(Long buildId, Long buildFloorId, State deviceState, LionPage lionPage);

    @Modifying
    @Transactional
    @Query(" update Device  set lastDataTime =:dateTime ,version=version+1 where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query(" update Device set deviceState =:state ,version=version+1 where id = :id ")
    public void updateState(@Param("id")Long id,@Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update Device set previousDisinfectDate =:previousDisinfectDate ,version=version+1 where id = :id ")
    public void updateDisinfectDate(@Param("id")Long id,@Param("previousDisinfectDate") LocalDate previousDisinfectDate);
}
