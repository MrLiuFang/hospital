package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.WashUser;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:48
 */
public interface WashUserDao extends BaseDao<WashUser> {
    /**
     * 根据洗手规则删除
     * @param washId
     * @return
     */
    public int deleteByWashId(Long washId);

    /**
     * 根据洗手规则查询
     * @param washId
     * @return
     */
    public List<WashUser> findByWashId(Long washId);

    /**
     * @param userId
     * @param type
     * @param washId
     * @return
     */
    @Query( " select wu from WashUser wu join Wash w on w.id = wu.washId where wu.userId = :userId and w.type <> :type and w.id <> :washId" )
    public List<WashUser> find(Long userId, WashRuleType type, Long washId);

    /**
     * @param type
     * @param isAllUser
     * @return
     */
    @Query( " select wu from WashUser wu join Wash w on w.id = wu.washId where wu.userId = :userId and w.type <> :type and w.isAllUser = :isAllUser" )
    public List<WashUser> find(WashRuleType type, Boolean isAllUser );


    /**
     * 根据用户查询洗手规则
     * @param userId
     * @return
     */
    public List<WashUser> findByUserId(Long userId);
}
