package com.lion.manage.dao.work;

import com.lion.core.LionPage;
import com.lion.manage.entity.work.Work;
import com.lion.upms.entity.enums.UserType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午10:28
 **/
public interface WorkDaoEx {

    /**
     *
     * @param departmentId
     * @param name
     * @param userType
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public Page<Map<String,Object>> List(Long departmentId, String name, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
