package com.lion.event.dao;

import com.lion.event.entity.CurrentPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午3:01
 **/
public interface CurrentPositionDao extends MongoRepository<CurrentPosition,String>,CurrentPositionDaoEx {

    /**
     * 根据类型和区域查询
     * @param typ
     * @param ri
     * @return
     */
    public List<CurrentPosition> findByTypAndRi(Integer typ, Long ri);
}
