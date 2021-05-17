package com.lion.manage.service.assets;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.dto.AddAssetsDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import sun.rmi.runtime.Log;

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
}
