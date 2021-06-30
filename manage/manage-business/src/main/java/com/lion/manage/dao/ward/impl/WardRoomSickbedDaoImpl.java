package com.lion.manage.dao.ward.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.ward.WardRoomSickbedDaoEx;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午9:52
 */
public class WardRoomSickbedDaoImpl implements WardRoomSickbedDaoEx {

    @Autowired
    private BaseDao<WardRoomSickbed> baseDao;

    @Override
    public Page<WardRoomSickbed> list(String bedCode, Long departmentId, Long wardId, Long wardRoomId, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select wrs from WardRoomSickbed wrs join WardRoom wr on wrs.wardRoomId = wr.id join Ward w on wr.wardId = w.id where 1=1");
        if (Objects.nonNull(departmentId) ) {
            sb.append(" and w.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(wardId) ) {
            sb.append(" and w.id = :wardId ");
            searchParameter.put("wardId",wardId);
        }
        if (Objects.nonNull(wardRoomId) ) {
            sb.append(" and wr.id = :wardRoomId ");
            searchParameter.put("wardRoomId",wardRoomId);
        }
        if (StringUtils.hasText(bedCode)) {
            sb.append(" and wrs.bedCode like :bedCode ");
            searchParameter.put("bedCode",bedCode);
        }
        sb.append(" order by wrs.createDateTime ");
        return (Page<WardRoomSickbed>) baseDao.findNavigator(lionPage, sb.toString(), searchParameter);
    }
}
