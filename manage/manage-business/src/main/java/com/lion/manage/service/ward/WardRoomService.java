package com.lion.manage.service.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.dto.AddWardRoomDto;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:15
 */
public interface WardRoomService extends BaseService<WardRoom> {

    /**
     * 根据病房基本信息删除
     * @param wardId
     * @return
     */
    public int deleteByWardId(Long wardId);

    /**
     * 保存病房房间
     * @param wardRoomDto
     * @param wardId
     */
    public void save(List<? extends WardRoom> wardRoomDto, Long wardId);

    /**
     * 根据病查询
     * @param wardId
     * @return
     */
    public List<WardRoom> find(Long wardId);

}
