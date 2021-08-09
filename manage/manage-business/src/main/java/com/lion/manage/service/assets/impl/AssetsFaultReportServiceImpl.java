package com.lion.manage.service.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.assets.AssetsFaultReportDao;
import com.lion.manage.entity.assets.AssetsFaultReport;
import com.lion.manage.entity.assets.dto.AddAssetsFaultReportDto;
import com.lion.manage.service.assets.AssetsFaultReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @create: 2021-08-09 10:21
 **/
@Service
public class AssetsFaultReportServiceImpl extends BaseServiceImpl<AssetsFaultReport> implements AssetsFaultReportService {

    @Autowired
    private AssetsFaultReportDao assetsFaultReportDao;

    @Override
    public AssetsFaultReport save(AddAssetsFaultReportDto addAssetsFaultReportDto) {
        AssetsFaultReport assetsFaultReport = new AssetsFaultReport();
        BeanUtils.copyProperties(addAssetsFaultReportDto,assetsFaultReport);
        return super.save(assetsFaultReport);
    }

    @Override
    public void update(AddAssetsFaultReportDto addAssetsFaultReportDto) {
        AssetsFaultReport assetsFaultReport = new AssetsFaultReport();
        BeanUtils.copyProperties(addAssetsFaultReportDto,assetsFaultReport);
        super.update(assetsFaultReport);
    }
}
