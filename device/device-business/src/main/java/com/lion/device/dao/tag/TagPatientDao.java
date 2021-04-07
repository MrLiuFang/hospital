package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagPatient;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:00
 */
public interface TagPatientDao extends BaseDao<TagPatient> {
    /**
     * 根据标签id删除
     * @param tagId
     * @return
     */
    public int deleteByTagId(Long tagId);
}
