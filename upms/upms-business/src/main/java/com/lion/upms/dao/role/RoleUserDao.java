package com.lion.upms.dao.role;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.role.RoleUser;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:16
 */
public interface RoleUserDao extends BaseDao<RoleUser> {

    /**
     * 查询角色关联了多少用户
     * @param roleId
     * @return
     */
    public Integer countByRoleId(Long roleId);

    /**
     * 根据角色id删除角色与用户的关联
     * @param roleId
     */
    public void deleteByRoleId(Long roleId);

}
