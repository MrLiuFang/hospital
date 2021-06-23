package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.assets.AssetsBorrowDao;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.expose.assets.AssetsBorrowExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/23 下午3:38
 */
@DubboService
public class AssetsBorrowExposeServiceImpl extends BaseServiceImpl<AssetsBorrow> implements AssetsBorrowExposeService {

    @Autowired
    private AssetsBorrowDao assetsBorrowDao;

    @Override
    public AssetsBorrow findNotReturn(Long assetsId) {
        return assetsBorrowDao.findFirstByAssetsIdAndReturnUserIdIsNull(assetsId);
    }
}
