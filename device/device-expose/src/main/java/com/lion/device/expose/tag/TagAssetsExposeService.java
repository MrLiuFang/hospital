package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagAssets;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:20
 */
public interface TagAssetsExposeService extends BaseService<TagAssets> {

    /**
     * 资产关联标签
     * @param assetsId
     * @param tagCode
     * @param departmentId
     * @return
     */
    public Boolean relation(Long assetsId,String tagCode,Long departmentId);

    /**
     * 解除绑定
     * @param assetsId
     * @return
     */
    public Boolean unrelation(Long assetsId);

    /**
     * 根据资产删除关联
     * @param assetsId
     * @return
     */
    public Boolean deleteByAssetsId(Long assetsId);
}
