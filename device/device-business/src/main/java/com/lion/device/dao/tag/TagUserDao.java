package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagUser;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:01
 */
public interface TagUserDao extends BaseDao<TagUser> {
    /**
     * 根据标签id删除
     * @param tagId
     * @return
     */
    public int deleteByTagId(Long tagId);
}
