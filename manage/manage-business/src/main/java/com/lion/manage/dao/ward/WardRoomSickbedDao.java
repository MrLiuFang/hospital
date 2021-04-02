package com.lion.manage.dao.ward;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:59
 */
public interface WardRoomSickbedDao extends BaseDao<WardRoomSickbed> {

    /**
     * 根据病房房间删除
     * @param wardRoomId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteByWardRoomId(Long wardRoomId);

    /**
     * 根据病房房间查询
     * @param wardRoomId
     * @return
     */
    public List<WardRoomSickbed> findByWardRoomId(Long wardRoomId);
}
