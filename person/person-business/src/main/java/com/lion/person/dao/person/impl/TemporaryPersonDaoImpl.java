package com.lion.person.dao.person.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.dao.person.TemporaryPersonDaoEx;
import com.lion.person.entity.person.TemporaryPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemporaryPersonDaoImpl implements TemporaryPersonDaoEx {

    @Autowired
    private BaseDao<TemporaryPerson> baseDao;

    @Override
    public List<TemporaryPerson> find(Long departmentId, String keyword, List<Long> ids) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select t from TemporaryPerson t where isLeave is false  ");
        Map<String, Object> searchParameter = new HashMap();
        if (Objects.nonNull(departmentId)) {
            sb.append(" and t.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(ids) && ids.size()>0) {
            sb.append(" and t.id in :ids ");
            searchParameter.put("ids",ids);
        }
        if (StringUtils.hasText(keyword)) {
            sb.append(" and ( t.idNo like :idNo or t.name like :name or t.phoneNumber like :phoneNumber or t.tagCode like :tagCode ) ");
            searchParameter.put("idNo","%"+keyword+"%");
            searchParameter.put("name","%"+keyword+"%");
            searchParameter.put("phoneNumber","%"+keyword+"%");
            searchParameter.put("tagCode", "%" + keyword + "%");
        }
        return (List<TemporaryPerson>) this.baseDao.findAll(sb.toString() ,searchParameter);
    }
}
