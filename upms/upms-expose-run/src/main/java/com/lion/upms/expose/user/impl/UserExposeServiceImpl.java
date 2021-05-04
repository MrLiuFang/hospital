package com.lion.upms.expose.user.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.enums.UserType;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.upms.service.user.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 用户远程RPC接口暴露实现
 * @author: Mr.Liu
 * @create: 2020-01-19 11:01
 */
@DubboService(interfaceClass = UserExposeService.class)
public class UserExposeServiceImpl extends BaseServiceImpl<User> implements UserExposeService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Override
    public User createUser(User user) {
        return userService.save(user);
    }

    @Override
    public User find(String username) {
        return userDao.findFirstByUsername(username);
    }

    @Override
    public Map<String,Object> find(Long departmentId, String name, UserType userType, List<Long> ontIn, int page, int size) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(departmentId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
        }
        if (Objects.nonNull(userType)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_userType",userType);
        }
        if (Objects.nonNull(ontIn) && ontIn.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.NOT_IN+"_id",ontIn);
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        LionPage lionPage = new LionPage(page,size, Sort.unsorted());
        lionPage.setJpqlParameter(jpqlParameter);
        Page<User> p = findNavigator(lionPage);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("totalElements",p.getTotalElements());
        map.put("list",p.getContent());
        return map;
    }
}
