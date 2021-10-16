package com.lion.manage.expose.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午11:48
 */
public interface WardRoomSickbedExposeService extends BaseService<WardRoomSickbed> {

    /**
     * 根据区域查询病床
     * @param regionId
     * @return
     */
    public List<WardRoomSickbed> find(Long regionId);

}
