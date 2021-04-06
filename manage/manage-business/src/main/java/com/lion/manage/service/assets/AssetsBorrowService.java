package com.lion.manage.service.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.dto.AddAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsBorrowDto;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:16
 */
public interface AssetsBorrowService extends BaseService<AssetsBorrow> {

    /**
     * 新增资产借用
     * @param addAssetsBorrowDto
     */
    public void add(AddAssetsBorrowDto addAssetsBorrowDto);

    /**
     * 修改资产借用
     * @param updateAssetsBorrowDto
     */
    public void update(UpdateAssetsBorrowDto updateAssetsBorrowDto);
}
