package com.lion.manage.dao.work;

import com.lion.core.LionPage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/12 下午10:28
 **/
public interface WorkDaoEx {

    /**
     *
     * @param departmentId
     * @param name
     * @param userTypeId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public Page<Map<String,Object>> List(Long departmentId, String name, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
