package com.lion.upms.dao.role;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.role.Role;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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

    /**
     * 根据编码查询角色
     * @param code
     * @return
     */
    public Role findFirstByCode(String code);

    /**
     * 查询用户关联的角色
     * @param userId
     * @return
     */
    @Query(" select r from  Role r join RoleUser ru on r.id = ru.roleId where ru.userId = :userId ")
    public Role findByUserId(Long userId);

    public List<Role> findByCodeIn(String... code);
}
