package com.lion.manage.expose.work;

import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.work.Work;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午8:16
 **/
public interface WorkExposeService extends BaseService<Work> {

    /**
     * 查询员工上下班
     * @param departmentId
     * @param name
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public PageResultData<Map<String,Object>> find(Long departmentId, String name, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
