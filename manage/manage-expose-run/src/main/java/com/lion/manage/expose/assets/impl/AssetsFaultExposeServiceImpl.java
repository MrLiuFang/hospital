package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.assets.AssetsFaultDao;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.expose.assets.AssetsFaultExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(interfaceClass = AssetsFaultExposeService.class)
public class AssetsFaultExposeServiceImpl extends BaseServiceImpl<AssetsFault> implements AssetsFaultExposeService {

    @Autowired
    private AssetsFaultDao assetsFaultDao;

    @Override
    public int countNotFinish(Long assetsId) {
        return assetsFaultDao.countByAssetsIdAndState(assetsId, AssetsFaultState.NOT_FINISHED);
    }

    @Override
    public AssetsFault find(Long assetsId, AssetsFaultState state) {
        return assetsFaultDao.findFirstByAssetsIdAndStateOrderByCreateDateTimeDesc(assetsId,AssetsFaultState.NOT_FINISHED);
    }
}
