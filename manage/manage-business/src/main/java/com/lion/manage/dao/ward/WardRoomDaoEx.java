package com.lion.manage.dao.ward;

import com.lion.core.LionPage;
import com.lion.manage.entity.ward.WardRoom;
import org.springframework.data.domain.Page;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/16 上午10:03
 */
public interface WardRoomDaoEx {

    /**
     * 列表
     * @param departmentId
     * @param wardId
     * @param code
     * @param lionPage
     * @return
     */
    public Page<WardRoom> list(Long departmentId, Long wardId,String code, LionPage lionPage);
}
