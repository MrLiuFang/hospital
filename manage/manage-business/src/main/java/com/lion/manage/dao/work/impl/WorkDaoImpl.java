package com.lion.manage.dao.work.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.work.WorkDaoEx;
import com.lion.manage.entity.work.Work;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

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

    public Page<Map<String,Object>> List(Long departmentId, String name, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append("select t1.id,t1.name,t2.start_work_time,t2.end_work_time from [upms].[dbo].[t_user] t1 left join [manage].[dbo].[t_work] t2 on t1.id = t2.user_id left join [manage].[dbo].[t_department_user] t3 on t1.id = t3.user_id where 1=1 ");
        if (Objects.nonNull(departmentId)) {
            sb.append(" and t3.id = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (StringUtils.hasText(name)) {
            sb.append(" and t1.name like :name ");
            searchParameter.put("name","%"+name+"%");
        }
        if (Objects.nonNull(userType)) {
            sb.append(" and t1.user_type = :userType ");
            searchParameter.put("userType",userType.getKey());
        }
        if (Objects.nonNull(startDateTime)){
            sb.append(" and t2.start_work_time >= :startDateTime  ");
            searchParameter.put("startDateTime",startDateTime);
        }

        if (Objects.nonNull(endDateTime)){
            sb.append(" and ( t2.end_work_time <= :endDateTime or t2.end_work_time is null )  ");
            searchParameter.put("endDateTime",endDateTime);
        }
        return (Page<Map<String,Object>>) baseDao.findNavigatorByNativeSql(lionPage, sb.toString(), searchParameter,null);
    }
}
