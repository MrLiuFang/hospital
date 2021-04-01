package com.lion.manage.dao.ward;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.ward.WardRoom;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:58
 */
public interface WardRoomDao extends BaseDao<WardRoom> {

    /**
     * 根据区域删除
     * @param regionId
     * @return
     */
    public int deleteByRegionId(Long regionId);

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
}
