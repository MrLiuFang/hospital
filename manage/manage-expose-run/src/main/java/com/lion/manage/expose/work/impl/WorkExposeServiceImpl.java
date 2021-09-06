package com.lion.manage.expose.work.impl;

import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.dao.work.WorkDao;
import com.lion.manage.entity.work.Work;
import com.lion.manage.expose.work.WorkExposeService;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午8:17
 **/
@DubboService(interfaceClass = WorkExposeService.class)
public class WorkExposeServiceImpl extends BaseServiceImpl<Work> implements WorkExposeService {

    @Autowired
    private WorkDao workDao;

    @Autowired
    private DepartmentUserDao departmentUserDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Override
    public PageResultData<Map<String, Object>> find(Long departmentId, String name, Long userTypeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        Page<Map<String, Object>> page = workDao.List(departmentId, name, userTypeId, startDateTime, endDateTime, lionPage);
        return new PageResultData(page.getContent(),lionPage,page.getTotalElements());
    }
}
