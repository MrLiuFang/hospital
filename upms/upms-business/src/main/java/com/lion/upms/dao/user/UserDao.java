package com.lion.upms.dao.user;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.user.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:15
 */
public interface UserDao extends BaseDao<User> {

    /**
     * 根据登陆账号查询用户
     * @param username
     * @return
     */
    public User findFirstByUsername(String username);

    /**
     * 查询角色关联的用户
     * @param roleId
     * @return
     */
    @Query(" select u from User u join RoleUser ru on u.id = ru.userId where ru.roleId = :roleId ")
    public List<User> findUserByRoleId(Long roleId);
}
