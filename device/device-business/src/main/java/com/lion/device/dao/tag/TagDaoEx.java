package com.lion.device.dao.tag;

import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;

import java.util.List;

public interface TagDaoEx {

    public List<Tag> find(Long departmentId, TagPurpose purpose, String keyWord, List<Long> listIds);
}
