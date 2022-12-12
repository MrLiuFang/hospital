package com.lion.device.dao.cctv.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.dao.cctv.CctvDaoEx;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CctvDaoImpl implements CctvDaoEx {

    @Autowired
    private BaseDao<Cctv> baseDao;


    @Override
    public List<Cctv> find(Long departmentId, String name, String code, String model, String cctvId) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<>();
        sb.append(" select c from Cctv c where ( c.name like :name or c.code like :code or c.model like :model or c.cctvId like :cctvId )  ");
        searchParameter.put("name", "%"+name+"%");
        searchParameter.put("code", "%"+code+"%");
        searchParameter.put("model", "%"+model+"%");
        searchParameter.put("cctvId", "%"+cctvId+"%");
        if (Objects.nonNull(departmentId) ) {
            sb.append(" and c.departmentId = :departmentId ");
            searchParameter.put("departmentId", departmentId);
        }
        return (List<Cctv>) baseDao.findAll(sb.toString(),searchParameter);
    }
}
