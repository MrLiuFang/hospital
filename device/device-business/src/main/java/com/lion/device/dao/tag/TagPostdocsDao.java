package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagPostdocs;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:00
 */
public interface TagPostdocsDao extends BaseDao<TagPostdocs> {
    /**
     * 根据标签id删除
     * @param tagId
     * @return
     */
    public int deleteByTagId(Long tagId);
}
