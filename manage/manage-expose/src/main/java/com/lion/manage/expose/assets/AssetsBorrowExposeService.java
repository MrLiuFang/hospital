package com.lion.manage.expose.assets;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsBorrow;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/23 下午3:37
 */
public interface AssetsBorrowExposeService extends BaseService<AssetsBorrow> {

    /**
     * 查找未归还的借用
     * @param assetsId
     * @return
     */
    public AssetsBorrow findNotReturn(Long assetsId);
}
