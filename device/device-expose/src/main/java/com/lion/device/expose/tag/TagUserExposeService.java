package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagUser;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 上午9:40
 **/
public interface TagUserExposeService extends BaseService<TagUser> {

    /**
     * 用户与标签绑定
     * @param userId
     * @param tagCode
     */
    public void binding(Long userId,String tagCode);

    /**
     * 用户与标签解绑
     * @param userId
     */
    public void unbinding(Long userId);

    /**
     *
     * @param tagId
     * @return 根据标签查询未解绑的关联
     */
    public TagUser find(Long tagId);
}
