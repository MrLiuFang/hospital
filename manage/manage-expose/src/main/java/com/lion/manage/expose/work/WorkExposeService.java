package com.lion.manage.expose.work;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.work.Work;
import com.lion.upms.entity.enums.UserType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午8:16
 **/
public interface WorkExposeService extends BaseService<Work> {

    /**
     * 查询员工上下班
     * @param departmentId
     * @param name
     * @param userType
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public PageResultData<Map<String,Object>> find(Long departmentId, String name, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
}
