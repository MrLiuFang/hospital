package com.lion.manage.expose.work.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.LionPage;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.department.DepartmentUserDao;
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
    private DepartmentUserDao departmentUserDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Override
    public Map<String, Object> find(Long departmentId, String name, UserType userType, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {
        Map<String,Object> map = userExposeService.find(departmentId,name,userType,null,1, 99999);
        List<User> userList = (List<User>) map.get("list");
        List<Long> userListId = new ArrayList<>();
        userList.forEach(user -> {
            userListId.add(user.getId());
        });

        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(userListId) && userListId.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.IN + "_userId", userListId);
        }
        if (Objects.nonNull(startDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO + "_startWorkTime", startDateTime);
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO + "_endWorkTime", endDateTime);
        }
        LionPage lionPage = new LionPage(page,size, Sort.unsorted());
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Work> p = findNavigator(lionPage);
        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("totalElements",p.getTotalElements());
        returnMap.put("list",p.getContent());
        return returnMap;
    }
}
