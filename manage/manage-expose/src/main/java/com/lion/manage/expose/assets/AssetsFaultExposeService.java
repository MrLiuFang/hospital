package com.lion.manage.expose.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.AssetsFaultState;

public interface AssetsFaultExposeService extends BaseService<AssetsFault> {

    /**
     * 统计没有完成故障
     * @param assetsId
     * @return
     */
    public int countNotFinish(Long assetsId);

    /**
     * 查询最后的故障
     * @param assetsId
     * @param state
     * @return
     */
    public AssetsFault find(Long assetsId, AssetsFaultState state);

}
