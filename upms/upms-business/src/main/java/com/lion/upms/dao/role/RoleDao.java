package com.lion.upms.dao.role;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.role.Role;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:15
 */
public interface RoleDao extends BaseDao<Role> {

    /**
     * 根据名称查询角色
     * @param name
     * @return
     */
    public Role findFirstByName(String name);
}
