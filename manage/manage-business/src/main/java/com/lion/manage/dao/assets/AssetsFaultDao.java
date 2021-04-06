package com.lion.manage.dao.assets;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsFault;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:14
 */
public interface AssetsFaultDao extends BaseDao<AssetsFault> {

    /**
     * 根据资产删除
     * @param assetsId
     * @return
     */
    public int deleteByAssetsId(Long assetsId);

    /**
     * 根据资产id查询总数
     * @param assetsId
     * @return
     */
    public Integer countByAssetsId(Long assetsId);
}
