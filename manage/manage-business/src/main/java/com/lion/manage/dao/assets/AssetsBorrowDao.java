package com.lion.manage.dao.assets;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsBorrow;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:13
 */
public interface AssetsBorrowDao extends BaseDao<AssetsBorrow> {

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

    /**
     * 查询资产借用是否未归还
     * @param assetsId
     * @return
     */
    public AssetsBorrow findFirstByAssetsIdAndReturnUserIdIsNull(Long assetsId);
}
