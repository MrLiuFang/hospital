package com.lion.manage.dao.work.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.work.WorkDaoEx;
import com.lion.manage.entity.work.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午10:29
 **/
public class WorkDaoImpl implements WorkDaoEx {

    @Autowired
    private BaseDao<Work> baseDao;

    public Page<Map<String,Object>> List(List<Long> departmentIds, List<Long> userIds, String name, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append("select t1.id as id ,t1.name as name,t2.start_work_time as start_work_time,t2.end_work_time as end_work_time from [upms].[dbo].[t_user] t1 left join [manage].[dbo].[t_work] t2 on t1.id = t2.user_id left join [manage].[dbo].[t_department_user] t3 on t1.id = t3.user_id where 1=1 ");
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            sb.append(" and t3.id in (:departmentId)");
            searchParameter.put("departmentId",departmentIds);
        }
        if (Objects.nonNull(userIds) && userIds.size()>0) {
            sb.append(" and t1.id in (:id)");
            searchParameter.put("id",userIds);
        }
        if (StringUtils.hasText(name)) {
            sb.append(" and t1.name like :name ");
            searchParameter.put("name","%"+name+"%");
        }
        if (Objects.nonNull(userTypeId)) {
            sb.append(" and t1.user_type_id = :userTypeId ");
            searchParameter.put("userTypeId", userTypeId);
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
