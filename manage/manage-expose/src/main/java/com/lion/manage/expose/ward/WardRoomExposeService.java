package com.lion.manage.expose.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.WardRoom;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午1:54
 */
public interface WardRoomExposeService extends BaseService<WardRoom> {

    /**
     * 根据区域查询病房
     * @param regionId
     * @return
     */
    public List<WardRoom> find(Long regionId);
}
