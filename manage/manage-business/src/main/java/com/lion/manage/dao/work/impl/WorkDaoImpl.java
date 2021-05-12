package com.lion.manage.dao.work.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.work.WorkDaoEx;
import com.lion.manage.entity.work.Work;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午10:29
 **/
public class WorkDaoImpl implements WorkDaoEx {

    @Autowired
    private BaseDao<Work> baseDao;

    @Override
    public Page<Work> list(List<Long> userId, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select w from Work w where 1=1  ");
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        if (Objects.nonNull(userId) && userId.size()>0) {
            sb.append(" and w.userId in :userId ");
            searchParameter.put("userId",userId);
        }
        if (Objects.nonNull(startDateTime)){
            sb.append(" and w.startWorkTime >= :startDateTime  ");
            searchParameter.put("startDateTime",startDateTime);
        }

        if (Objects.nonNull(endDateTime)){
            sb.append(" and ( w.endWorkTime <= :endDateTime or w.endWorkTime is null )  ");
            searchParameter.put("endDateTime",endDateTime);
        }
        return (Page<Work>) baseDao.findNavigator(PageRequest.of(page,size), sb.toString(), searchParameter);
    }
}
