package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagAssets;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:27
 */
public interface TagAssetsDao extends BaseDao<TagAssets> {

    /**
     * 根据标签id删除
     * @param tagId
     * @return
     */
    public int deleteByTagId(Long tagId);

    /**
     * 根据资产删除关联
     * @param assetsId
     * @return
     */
    public int deleteByAssetsId(Long assetsId);

    /**
     *
     * @param assetsId
     * @return
     */
    public TagAssets findFirstByAssetsIdAndUnbindingTimeIsNull(Long assetsId);
}
