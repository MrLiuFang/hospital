package com.lion.manage.dao.assets;

import com.lion.core.LionPage;
import com.lion.manage.entity.enums.State;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午11:17
 */
public interface AssetsDaoEx {

    Page list(String name, Long borrowUserId, List<Long> departmentIds, Long assetsTypeId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage);

    /**
     * 统计
     * @param departmentId
     * @param deviceState
     * @param assetsIds
     * @return
     */
    public Integer count(Long departmentId, State deviceState, List<Long> assetsIds);
}
