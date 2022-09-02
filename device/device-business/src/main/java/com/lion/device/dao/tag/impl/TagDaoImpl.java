package com.lion.device.dao.tag.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.dao.tag.TagDaoEx;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TagDaoImpl implements TagDaoEx {

    @Autowired
    private BaseDao<Tag> baseDao;


    @Override
    public List<Tag> find(Long departmentId, TagPurpose purpose, String keyWord, List<Long> listIds) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<>();
        sb.append(" select t from Tag t where 1=1 ");
        if (Objects.nonNull(departmentId)) {
            sb.append(" and t.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(purpose)) {
            sb.append(" and t.purpose = :purpose ");
            searchParameter.put("purpose",purpose);
        }
        if (Objects.nonNull(listIds) && listIds.size()>0){
            sb.append(" and t.id in :ids ");
            searchParameter.put("ids",listIds);
        }
        if (StringUtils.hasText(keyWord)) {
            sb.append(" and  ( t.tagCode like :tagCode or  )  ");
            searchParameter.put("tagCode","%" +keyWord +"%");
        }
        return (List<Tag>) baseDao.findAll(sb.toString(),searchParameter);
    }
}
