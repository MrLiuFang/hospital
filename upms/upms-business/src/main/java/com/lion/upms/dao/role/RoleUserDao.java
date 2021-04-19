package com.lion.upms.dao.role;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.role.RoleUser;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 根据用户ID删除角色关联
     * @param userId
     */
    @Transactional(propagation= Propagation.REQUIRES_NEW)
    public void deleteByUserId(Long userId);

    /**
     * 根据角色查询
     * @param roleId
     * @return
     */
    public List<RoleUser> findByRoleId(Long roleId);

}
