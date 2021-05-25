package com.lion.device.dao.tag;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagPatient;
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

    /**
     * 根据流动人员删除
     * @param postdocsId
     * @return
     */
    public int deleteByPostdocsId(Long postdocsId);

    /**
     * 根据标签id查询
     * @param tagId
     * @return
     */
    public TagPostdocs findFirstByTagIdAndUnbindingTimeIsNull(Long tagId);

    /**
     * 根据流动人员查询关联关系
     * @param postdocsId
     * @return
     */
    public TagPostdocs findFirstByPostdocsIdAndUnbindingTimeIsNull(Long postdocsId);
}
