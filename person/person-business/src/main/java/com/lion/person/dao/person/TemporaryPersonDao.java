package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.person.TemporaryPerson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:04
 */
public interface TemporaryPersonDao extends BaseDao<TemporaryPerson> {

    @Modifying
    @Transactional
    @Query(" update TemporaryPerson set deviceSate =:state where id = :id ")
    public void update(Long id,Integer state);

    @Modifying
    @Transactional
    @Query(" update TemporaryPerson  set lastDataTime =:dateTime where id = :id ")
    public void update(Long id, LocalDateTime dateTime);
}
