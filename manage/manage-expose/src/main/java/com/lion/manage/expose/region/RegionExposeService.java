package com.lion.manage.expose.region;

import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.vo.ListRegionVo;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/24 下午2:46
 **/
public interface RegionExposeService extends BaseService<Region> {

//    /**
//     * 根据设备组ID查询关联的区域
//     * @param deviceGroupId
//     * @return
//     */
//    public  Region find(Long deviceGroupId);

//    /**
//     * 删除区域关联的设备组（将deviceGroupId字段置为null）
//     * @param deviceGroupId
//     */
//    public void deleteDeviceGroup(Long deviceGroupId);

    /**
     * 根据楼成查村区域
     * @param buildFloorId
     * @return
     */
    public List<Region> findByBuildFloorId(Long buildFloorId);

    /**
     * 根据科室查询区域
     * @param departmentId
     * @return
     */
    public List<Region> findByDepartmentId(Long departmentId);

    public List<Region> findByDepartmentIds(List<Long> departmentIds);

    /**
     *
     * @param lionPage
     * @return
     */
    public PageResultData<List<Region>> find(LionPage lionPage);

    /**
     *
     * @param keyword
     * @param departmentId
     * @return
     */
    public List<ListRegionVo> find(String keyword, Long departmentId);

    public Optional<Region> find(String name);
}
