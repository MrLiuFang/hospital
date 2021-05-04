package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.TagUser;

import java.util.List;

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

    /**
     * 根据用户ID 删除关联关系
     * @param userId
     * @return
     */
    public int deleteByUserId(Long userId);

    /**
     * 根据标签查询
     * @param tagId
     * @return
     */
    public TagUser findFirstByTagIdAndUnbindingTimeIsNull(Long tagId);

    /**
     * 根据userid查村未解绑定的标签关联关系
     * @param userId
     * @return
     */
    public TagUser findFirstByUserIdAndUnbindingTimeIsNull(Long userId);

}
