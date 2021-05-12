package com.lion.manage.service.region;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:07
 */
public interface RegionService extends BaseService<Region> {

    /**
     * 根据科室查询区域
     * @param departmentId
     * @return
     */
    public List<Region> find(Long departmentId);

    /**
     * 根据建筑楼层查询
     * @param buildFloorId
     * @return
     */
    public List<Region> findByBuildFloorId(Long buildFloorId);

    /**
     * 新增区域
     * @param addRegionDto
     */
    public void add(AddRegionDto addRegionDto);

    /**
     * 修改区域
     * @param updateRegionDto
     */
    public void update(UpdateRegionDto updateRegionDto);

    /**
     * 修改区域范围坐标
     * @param updateRegionCoordinatesDto
     */
    public void updateCoordinates(UpdateRegionCoordinatesDto updateRegionCoordinatesDto);

    /**
     * 删除
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);


}
