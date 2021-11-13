package com.lion.manage.service.region;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.dto.AddRegionDto;
import com.lion.manage.entity.region.dto.BatchUpdateWashTemplateDto;
import com.lion.manage.entity.region.dto.UpdateRegionCoordinatesDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.DetailsRegionVo;
import com.lion.manage.entity.region.vo.ListRegionVo;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
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
     * 批量更新
     * @param batchUpdateWashTemplateDto
     */
    public void batchUpdateWashTemplate( BatchUpdateWashTemplateDto batchUpdateWashTemplateDto);

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


    /**
     * 详情
     * @param id
     * @return
     */
    DetailsRegionVo details( Long id);

    /**
     * 列表
     * @param name
     * @param code
     * @param departmentIds
     * @param washTemplateId
     * @param regionTypeId
     * @param buildId
     * @param buildFloorId
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListRegionVo>> list(String name,String code,List<Long> departmentIds, Long washTemplateId, Long regionTypeId,Long buildId, Long buildFloorId, LionPage lionPage);

}