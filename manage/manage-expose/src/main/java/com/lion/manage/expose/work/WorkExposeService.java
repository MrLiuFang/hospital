package com.lion.manage.expose.work;

import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.work.Work;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午8:16
 **/
public interface WorkExposeService extends BaseService<Work> {

    /**
     * 查询员工上下班
     *
     * @param departmentIds
     * @param userIds
     * @param name
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public PageResultData<Map<String,Object>> find(List<Long> departmentIds, List<Long> userIds, String name, List<Long> userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
