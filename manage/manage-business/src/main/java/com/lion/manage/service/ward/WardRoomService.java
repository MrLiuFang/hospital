package com.lion.manage.service.ward;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.WardRoom;

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
}
