package com.lion.manage.dao.ward;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.manage.entity.ward.WardRoom;
import org.springframework.data.domain.Page;

import java.util.List;

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
     * @param lionPage
     * @return
     */
    public Page<WardRoom> list(Long departmentId, Long wardId, LionPage lionPage);
}
