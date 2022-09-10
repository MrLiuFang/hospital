package com.lion.manage.dao.department.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.department.DepartmentDaoEx;
import com.lion.manage.entity.department.Department;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DepartmentDaoImpl implements DepartmentDaoEx {

    @Autowired
    private BaseDao<Department> baseDao;

    @Override
    public List<Department> findAllParent(String name) {
        StringBuilder sql = new StringBuilder("with cte_parent(id,name,parent_id,create_date_time,create_user_id,update_date_time,update_user_id,version,remarks) " +
                "as " +
                "( " +
                "  select id,name,parent_id,create_date_time,create_user_id,update_date_time,update_user_id,version,remarks" +
                "  from t_department" +
                "  where name like '%"+name+"%'" +
                "  union all " +
                "  select a.id,a.name,a.parent_id,a.create_date_time,a.create_user_id,a.update_date_time,a.update_user_id,a.version,a.remarks" +
                "  from t_department a " +
                "  inner join  " +
                "  cte_parent b " +
                "  on a.id=b.parent_id" +
                ")             " +
                "select distinct * from cte_parent ");
        return baseDao.getSession().createNativeQuery(sql.toString(),Department.class).getResultList();
    }
}
