package com.lion.manage.service.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.WardRoomSickbed;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:17
 */
public interface WardRoomSickbedService extends BaseService<WardRoomSickbed> {

    /**
     * 保存病床
     * @param wardRoomSickbedDto
     * @param wardRoomId
     */
    public void save(List<? extends WardRoomSickbed> wardRoomSickbedDto, Long wardRoomId);

    /**
     * 根据病房房间查询
     * @param wardRoomId
     * @return
     */
    public List<WardRoomSickbed> find(Long wardRoomId);
}
