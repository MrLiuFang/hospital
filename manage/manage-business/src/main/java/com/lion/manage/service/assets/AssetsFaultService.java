package com.lion.manage.service.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.AddAssetsFaultDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsFaultDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsFaultVo;

import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:19
 */
public interface AssetsFaultService extends BaseService<AssetsFault> {

    /**
     * 新增资产故障
     * @param addAssetsFaultDto
     */
    public void add(AddAssetsFaultDto addAssetsFaultDto);

    /**
     * 修改资产故障
     * @param updateAssetsFaultDto
     */
    public void update(UpdateAssetsFaultDto updateAssetsFaultDto);

    /**
     * 详情
     * @param id
     * @return
     */
    DetailsAssetsFaultVo details(Long id);
}
