package com.lion.manage.dao.ward;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:59
 */
public interface WardRoomSickbedDao extends BaseDao<WardRoomSickbed> ,WardRoomSickbedDaoEx {

    /**
     * 根据病房房间删除
     * @param wardRoomId
     * @return
     */
    public int deleteByWardRoomId(Long wardRoomId);

    /**
     * 根据病房房间查询
     * @param wardRoomId
     * @return
     */
    public List<WardRoomSickbed> findByWardRoomId(Long wardRoomId);

    @Modifying
    @Transactional
    @Query( " update WardRoomSickbed set regionId =:regionId where id in :ids " )
    public int updateRegionId(Long regionId,List<Long> ids);

    @Modifying
    @Transactional
    @Query( " update WardRoomSickbed set regionId = null where regionId = :regionId " )
    public int updateRegionIdIsNull(Long regionId);

    @Modifying
    @Transactional
    @Query( " update WardRoomSickbed set regionId = null where id in :ids " )
    public int updateRegionIdIsNull1(List<Long> ids);

    /**
     * 根据区域查询
     * @param regionId
     * @return
     */
    public List<WardRoomSickbed> findByRegionId(Long regionId);

    /**
     * 根据病房房间统计
     * @param wardRoomId
     * @return
     */
    public int countByWardRoomId(Long wardRoomId);
}
