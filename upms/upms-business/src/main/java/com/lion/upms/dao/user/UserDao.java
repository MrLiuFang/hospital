package com.lion.upms.dao.user;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.entity.enums.State;
import com.lion.upms.entity.user.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
     *
     * @param ids
     * @return
     */
    public List<User> findByIdIn(List<Long> ids);

    /**
     *
     * @param name
     * @param ids
     * @return
     */
    public List<User> findByNameLikeAndIdIn(String name,List<Long> ids);

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

//    @Override
//    Optional<User> findById(Serializable serializable);

    /**
     * 查询角色关联的用户
     * @param roleId
     * @return
     */
    @Query(" select u from User u join RoleUser ru on u.id = ru.userId where ru.roleId = :roleId ")
    public List<User> findUserByRoleId(Long roleId);

    @Modifying
    @Transactional
    @Query(" update User  set deviceState =:state ,version = version+1 where id = :id ")
    public void updateSate(@Param("id") Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update User  set lastDataTime =:dateTime ,version = version+1 where id = :id ")
    public void updateLastDataTime(@Param("id") Long id, @Param("dateTime") LocalDateTime dateTime);

    @Query( " select id from User " )
    public List<Long> findAllId();

    /**
     * 统计
     * @param ids
     * @param deviceState
     * @return
     */
    public int countByIdInAndDeviceState(List<Long> ids, State deviceState);

    public int countByIdIn(List<Long> ids);
    /**
     * 统计用户类型
     * @param userTypeIds
     * @return
     */
    public int countByUserTypeIdIn(Collection<Long> userTypeIds);

    /**
     * 统计用户类型
     * @param userTypeId
     * @return
     */
    public int countByUserTypeId(Long userTypeId);

    /**
     * 根据用户类型查寻
     * @param userTypeIds
     * @return
     */
    public List<User> findByUserTypeIdIn(Collection<Long> userTypeIds);
}
