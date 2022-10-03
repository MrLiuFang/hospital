package com.lion.manage.dao.assets;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.enums.AssetsFaultState;

import java.util.List;

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

    /**
     * 统计没有完成故障
     * @param assetsId
     * @param state
     * @return
     */
    public int countByAssetsIdAndState(Long assetsId,AssetsFaultState state);

    /**
     * 查询最后的故障
     * @param assetsId
     * @param state
     * @return
     */
    public AssetsFault findFirstByAssetsIdAndStateOrderByCreateDateTimeDesc(Long assetsId,AssetsFaultState state);

    public List<AssetsFault> findByCodeLikeOrDescribeLike(String code,String describe);

}
