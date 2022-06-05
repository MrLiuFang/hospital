package com.lion.manage.dao.ward;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.ward.WardRoom;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:58
 */
public interface WardRoomDao extends BaseDao<WardRoom> , WardRoomDaoEx{


    /**
     * 根据病房基本信息删除
     * @param wardId
     * @return
     */
    public int deleteByWardId(Long wardId);

    /**
     * 根就病房基本信息查询房间
     * @param wardId
     * @return
     */
    public List<WardRoom> findByWardId(Long wardId);

    @Modifying
    @Transactional
    @Query( " update WardRoom set regionId =:regionId where id in :ids " )
    public int updateRegionId(Long regionId,List<Long> ids);

    @Modifying
    @Transactional
    @Query( " update WardRoom set regionId = null where regionId = :regionId " )
    public int updateRegionIdIsNull(Long regionId);

    /**
     * 根据区域查寻
     * @param reginId
     * @return
     */
    public List<WardRoom> findByRegionId(Long reginId);

    public List<WardRoom> findByIdIn(List<Long> ids);
}
