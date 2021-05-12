package com.lion.manage.expose.work.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.LionPage;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentUserDao;
import com.lion.manage.dao.work.WorkDao;
import com.lion.manage.entity.work.Work;
import com.lion.manage.expose.work.WorkExposeService;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

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
    public Map<String, Object> find(Long departmentId, String name, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {
        Map<String,Object> map = userExposeService.find(departmentId,name,userType,null,1, 999999999);
        List<User> userList = (List<User>) map.get("list");
        List<Long> userListId = new ArrayList<>();
        userList.forEach(user -> {
            userListId.add(user.getId());
        });
        Page<Work> p = workDao.list(userListId,startDateTime,endDateTime,page,size);
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("totalElements",p.getTotalElements());
        returnMap.put("list",p.getContent());
        return returnMap;
    }
}
