package com.lion.manage.dao.ward.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.ward.WardRoomDaoEx;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:03
 */
public class WardRoomDaoImpl implements WardRoomDaoEx {

    @Autowired
    private BaseDao<WardRoom> baseDao;

    @Override
    public Page<WardRoom> list(Long departmentId, Long wardId, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select wr from WardRoom wr join Ward w on wr.wardId = w.id where 1=1");
        if (Objects.nonNull(departmentId) ) {
            sb.append(" and w.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(wardId) ) {
            sb.append(" and w.id = :wardId ");
            searchParameter.put("wardId",wardId);
        }
        sb.append(" order by wr.createDateTime ");
        return (Page<WardRoom>) baseDao.findNavigator(lionPage, sb.toString(), searchParameter);
    }
}
