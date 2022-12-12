package com.lion.manage.dao.region.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.region.RegionDaoEx;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.region.Region;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegionDaoImpl implements RegionDaoEx {

    @Autowired
    private BaseDao<Region> baseDao;


    @Override
    public List<Region> find(String name, String code, Long departmentId) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select a from Region a where ( a.name like :name or a.code like :code ) ");
        searchParameter.put("name","%"+name+"%");
        searchParameter.put("code","%"+code+"%");
        if (Objects.nonNull(departmentId)){
            sb.append(" and a.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        List<Region> list = (List<Region>) baseDao.findAll(sb.toString(),searchParameter);
        return list;
    }
}
