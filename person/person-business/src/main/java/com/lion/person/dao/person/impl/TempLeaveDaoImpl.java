package com.lion.person.dao.person.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.dao.person.TempLeaveDaoEx;
import com.lion.person.entity.person.TempLeave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/9 下午8:48
 */
public class TempLeaveDaoImpl implements TempLeaveDaoEx {

    @Autowired
    private BaseDao<TempLeave> baseDao;


    @Override
    public Page list(String tagCode, Long departmentId, Long patientId, Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, String ids, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append("select tl from TempLeave tl join Patient p on tl.patientId = p.id where tl.isClosure=false and tl.endDateTime >= :now ");
        searchParameter.put("now",LocalDateTime.now());
        if (StringUtils.hasText(ids)) {
            List<Long> _ids = new ArrayList<>();
            String[] str= ids.split(",");
            for (String id : str) {
                _ids.add(Long.valueOf(id));
            }
            if (_ids.size()>0){
                sb.append(" and p.id in :ids ");
                searchParameter.put("ids",_ids);
            }
        }
        if (StringUtils.hasText(tagCode)) {
            sb.append(" and p.tagCode like :tagCode ");
            searchParameter.put("tagCode",tagCode);
        }
        if (Objects.nonNull(departmentId)) {
            sb.append(" and p.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(patientId)) {
            sb.append(" and p.id = :patientId ");
            searchParameter.put("patientId",patientId);
        }
        if (Objects.nonNull(userId)) {
            sb.append(" and tl.userId = :userId ");
            searchParameter.put("userId",userId);
        }
        if (Objects.nonNull(startDateTime)) {
            sb.append(" and tl.createDateTime >= :startDateTime ");
            searchParameter.put("startDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)) {
            sb.append(" and tl.createDateTime <= :endDateTime ");
            searchParameter.put("endDateTime",endDateTime);
        }
        sb.append(" and tl.isClosure = false ");
        sb.append(" order by tl.createDateTime desc");
        return baseDao.findNavigator(lionPage, sb.toString(), searchParameter);
    }
}
