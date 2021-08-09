package com.lion.manage.service.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsFaultReport;
import com.lion.manage.entity.assets.dto.AddAssetsFaultReportDto;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 10:21
 **/
public interface AssetsFaultReportService extends BaseService<AssetsFaultReport> {

    /**
     * 新增资产故障汇报
     * @param addAssetsFaultReportDto
     * @return
     */
    public AssetsFaultReport save(AddAssetsFaultReportDto addAssetsFaultReportDto);

    /**
     * 修改资产故障汇报
     * @param addAssetsFaultReportDto
     */
    public void update(AddAssetsFaultReportDto addAssetsFaultReportDto);
}
