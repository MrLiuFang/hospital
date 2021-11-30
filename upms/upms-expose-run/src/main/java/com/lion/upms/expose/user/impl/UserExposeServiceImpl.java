package com.lion.upms.expose.user.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.LionPage;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.enums.State;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.upms.service.user.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @description: 用户远程RPC接口暴露实现
 * @author: Mr.Liu
 * @create: 2020-01-19 11:01
 */
@DubboService(interfaceClass = UserExposeService.class)
public class UserExposeServiceImpl extends BaseServiceImpl<User> implements UserExposeService {

    @Autowired
    private UserService userService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

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
    public int count(List<Long> ids, State deviceState) {
        return userDao.countByIdInAndAndDeviceState(ids,deviceState);
    }

    @Override
    public int countInUserTypeId(Collection<Long> userTypeIds) {
        return userDao.countByUserTypeIdIn(userTypeIds);
    }

    @Override
    public List<User> find(Collection<Long> userTypeIds) {
        return userDao.findByUserTypeIdIn(userTypeIds);
    }

    @Override
    public User find(Integer number) {
        return userDao.findFirstByNumber(number);
    }

    @Override
    public List<User> findByName(String name) {
        return userDao.findByNameLike("%"+name+"%");
    }

    @Override
    public List<User> findInIds(List<Long> ids) {
        return userDao.findByIdIn(ids);
    }

    @Override
    public List<User> findByNameAndInIds(String name, List<Long> ids) {
        return userDao.findByNameLikeAndIdIn("%"+name+"%", ids);
    }

    @Override
    public Map<String,Object> find(Long departmentId, String name, Long userTypeId, List<Long> ontIn, int page, int size) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(departmentId)){
            List<Long> list = departmentUserExposeService.findAllUser(departmentId);
            if (Objects.nonNull(list) && list.size()>0 ) {
                jpqlParameter.setSearchParameter(SearchConstant.IN + "_id", list);
            }
        }
        if (Objects.nonNull(userTypeId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_userTypeId", userTypeId);
        }
        if (Objects.nonNull(ontIn) && ontIn.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.NOT_IN+"_id",ontIn);
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        LionPage lionPage = new LionPage(page,size);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<User> p = findNavigator(lionPage);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("totalElements",p.getTotalElements());
        map.put("list",p.getContent());
        return map;
    }

    @Override
    public void updateState(Long id, Integer state) {
        userDao.updateSate(id, State.instance(state));
    }

    @Override
    public List<Long> allId() {
        return userDao.findAllId();
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        userDao.updateLastDataTime(id,dateTime);
    }

}
