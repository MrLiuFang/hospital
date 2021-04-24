package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.Tag;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:05
 */
public interface TagExposeService extends BaseService<Tag> {

    /**
     * 根据资产id查询正在关联标签
     * @param assetsId
     * @return
     */
    public Tag find(Long assetsId);

    /**
     * 根据标签编码查询
     * @param tagCode
     * @return
     */
    public Tag find(String tagCode);


}
