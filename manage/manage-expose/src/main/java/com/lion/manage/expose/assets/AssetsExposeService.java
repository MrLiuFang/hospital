package com.lion.manage.expose.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 上午9:49
 **/
public interface AssetsExposeService extends BaseService<Assets> {

    /**
     * 根据标签查村资产
     * @param tagId
     * @return
     */
    public Assets find(Long tagId);

    /**
     * 根据编码查询
     * @param code
     * @return
     */
    public Assets find(String code);

    /**
     * 根据楼层统计区域内的资产数量
     * @param buildFloorId
     * @return
     */
    public List<Map<String, Object>> count(Long buildFloorId);

    /**
     * 统计科室内的资产数量
     * @param departmentId
     * @return
     */
    public Integer countByDepartmentId(Long departmentId);

    /**
     * 查询部门内的资产
     * @param departmentId
     * @return
     */
    public List<Assets> findByDepartmentId(Long departmentId);

    /**
     * 查询部门内的资产
     * @param departmentId
     * @param name
     * @param code
     * @return
     */
    public List<Assets> findByDepartmentId(Long departmentId,String name,String code);

    /**
     * 获取所有数据的id
     * @return
     */
    public List<Long> allId();

    /**
     * 修改状态
     * @param id
     * @param state
     */
    public void updateState(Long id,Integer state);

    /**
     * 更新设备数据上传时间
     * @param id
     * @param dateTime
     */
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime);
}
