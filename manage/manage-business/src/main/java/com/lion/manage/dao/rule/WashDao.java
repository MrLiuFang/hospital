package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.Wash;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午4:47
 */
public interface WashDao extends BaseDao<Wash> {

    /**
     * 根据名称查询
     * @param name
     * @return
     */
    public Wash findFirstByName(String name);

    /**
     * 根据区域和用户查询洗手规则
     * @param regionId
     * @param userId
     * @return
     */
    @Query( " select distinct w from Wash w join WashUser wu on w.id = wu.washId join WashRegion wr on w.id = wr.washId where wu.userId = :userId and wr.regionId = :regionId " )
    public Wash find(Long regionId, Long userId);

    /**
     * 根据区域查询洗手规则
     * @param regionId
     * @return
     */
    @Query( " select distinct w from Wash w join WashRegion wr on w.id = wr.washId where wr.regionId = :regionId " )
    public List<Wash> find(Long regionId);

    /**
     * 查询用户所有的定时洗手规则
     * @param userId
     * @param type
     * @return
     */
    @Query( " select distinct w from Wash w join WashUser wu on w.id = wu.washId where wu.userId = :userId and w.type = :type  " )
    public List<Wash> findLoopWash(Long userId, WashRuleType type);
}
