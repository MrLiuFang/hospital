package com.lion.manage.service.assets;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.dto.AddAssetsDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:14
 */
public interface AssetsService extends BaseService<Assets> {

    /**
     * 新增资产
     * @param addAssetsDto
     */
    public void add(AddAssetsDto addAssetsDto);

    /**
     * 修改资产
     * @param updateAssetsDto
     */
    public void update(UpdateAssetsDto updateAssetsDto);

    /**
     * 删除资产
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);

    /**
     * 资产详情
     * @param id
     * @return
     */
    public DetailsAssetsVo details(Long id);

    /**
     * 根据标签id查询资产
     * @param tagId
     * @return
     */
    public Assets findByTagId(Long tagId);

    /**
     * 根据科室查询资产
     * @param departmentIds
     * @return
     */
    public List<Assets> findByDepartmentId(List<Long> departmentIds);

    /**
     * 根据资产编码查询
     * @param code
     * @return
     */
    public List<Assets> find(String code);

    /**
     * 根据关键字查询
     * @param keyword
     * @return
     */
    public List<Assets> findByKeyword(String keyword);

}
