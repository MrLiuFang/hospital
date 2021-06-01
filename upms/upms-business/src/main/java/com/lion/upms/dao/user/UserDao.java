package com.lion.upms.dao.user;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.user.User;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/22下午9:15
 */
public interface UserDao extends BaseDao<User>, UserDaoEx {

    /**
     * 根据登陆账号查询用户
     * @param username
     * @return
     */
    public User findFirstByUsername(String username);

    /**
     * 根据姓名模糊查询
     * @param name
     * @return
     */
    public List<User> findByNameLike(String name);

    /**
     * 根据邮箱查询用户
     * @param email
     * @return
     */
    public User findFirstByEmail(String email);

    /**
     * 根据员工号查询用户
     * @param number
     * @return
     */
    public User findFirstByNumber(Integer number);

    @Override
    Optional<User> findById(Serializable serializable);

    /**
     * 查询角色关联的用户
     * @param roleId
     * @return
     */
    @Query(" select u from User u join RoleUser ru on u.id = ru.userId where ru.roleId = :roleId ")
    public List<User> findUserByRoleId(Long roleId);

    @Modifying
    @Transactional
    @Query(" update User  set deviceSate =:state where id = :id ")
    public void update(Long id,Integer state);

    @Modifying
    @Transactional
    @Query(" update User  set lastDataTime =:dateTime where id = :id ")
    public void update(Long id, LocalDateTime dateTime);

    @Query( " select id from User " )
    public List<Long> findAllId();


}
