package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.enums.State;
import com.lion.person.entity.person.TemporaryPerson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:04
 */
public interface TemporaryPersonDao extends BaseDao<TemporaryPerson> ,TemporaryPersonDaoEx {

    @Modifying
    @Transactional
    @Query(" update TemporaryPerson set deviceState =:state  ,version=version +1 where id = :id ")
    public void updateState(@Param("id")Long id, @Param("state") State state);

    @Modifying
    @Transactional
    @Query(" update TemporaryPerson  set lastDataTime =:dateTime ,version = version+1 where id = :id ")
    public void updateLastDataTime(@Param("id")Long id, @Param("dateTime")LocalDateTime dateTime);

    @Modifying
    @Transactional
    @Query(" update TemporaryPerson  set isWaitLeave =:isWaitLeave ,tagCode = null ,version = version+1 where id = :id ")
    public void updateIsWaitLeave(@Param("id")Long id, @Param("isWaitLeave") Boolean isWaitLeave);


    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @return
     */
    public int countByDepartmentIdAndIsLeave(Long departmentId, Boolean isLeave);

    /**
     *
     * @param departmentId
     * @param isLeave
     * @param ids
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndIdIn(Long departmentId, Boolean isLeave,List<Long> ids);

    /**
     * 统计
     * @param departmentId
     * @param isLeave
     * @param deviceState
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndDeviceState(Long departmentId, Boolean isLeave, State deviceState);

    /**
     *
     * @param departmentId
     * @param isLeave
     * @param deviceState
     * @param ids
     * @return
     */
    public int countByDepartmentIdAndIsLeaveAndDeviceStateAndIdIn(Long departmentId, Boolean isLeave, State deviceState,List<Long> ids);

    /**
     * 根据科室查询
     * @param departmentId
     * @param isLeave
     * @param name
     * @return
     */
    public List<TemporaryPerson> findByDepartmentIdAndIsLeaveAndNameLike(Long departmentId, Boolean isLeave, String name);

    /**
     *
     * @param departmentId
     * @param isLeave
     * @param name
     * @param ids
     * @return
     */
    public List<TemporaryPerson> findByDepartmentIdAndIsLeaveAndNameLikeAndIdIn(Long departmentId, Boolean isLeave, String name,List<Long> ids);

    /**
     * 根据科室查询
     * @param departmentId
     * @param isLeave
     * @return
     */
    public List<TemporaryPerson> findByDepartmentIdAndIsLeave(Long departmentId, Boolean isLeave);

    /**
     *
     * @param departmentId
     * @param isLeave
     * @param ids
     * @return
     */
    public List<TemporaryPerson> findByDepartmentIdAndIsLeaveAndIdIn(Long departmentId, Boolean isLeave,List<Long> ids);

    int countByCreateDateTimeGreaterThanEqualAndDepartmentIdIn(LocalDateTime startDateTime,List<Long> departmentIds);

    int countByIsLeaveIsFalseAndDepartmentIdIn(List<Long> departmentIds);

}
