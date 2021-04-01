package com.lion.manage.service.region;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.dto.AddRegionDto;
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
     * 新增区域
     * @param updateRegionDto
     */
    public void add(UpdateRegionDto updateRegionDto);

    /**
     * 删除
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);


}
