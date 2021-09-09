package com.lion.event.dao;

import com.lion.event.entity.CurrentPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.plaf.ViewportUI;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description
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

    /**
     * 删除工作热源/病人/流动人员
     * @param pi
     */
    public void deleteByPi(Long pi);

    /**
     * 删除设备/资产
     * @param adi
     */
    public void deleteByAdi(Long adi);

    /**
     * 删除标签
     * @param ti
     */
    public void deleteByTi(Long ti);
}
