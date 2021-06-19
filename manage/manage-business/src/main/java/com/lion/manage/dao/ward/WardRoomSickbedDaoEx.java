package com.lion.manage.dao.ward;

import com.lion.core.LionPage;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.data.domain.Page;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午9:52
 */
public interface WardRoomSickbedDaoEx {

    /**
     * 列表
     *
     * @param bedCode
     * @param departmentId
     * @param wardId
     * @param wardRoomId
     * @param lionPage
     * @return
     */
    public Page<WardRoomSickbed> list(String bedCode,Long departmentId, Long wardId, Long wardRoomId, LionPage lionPage);
}
