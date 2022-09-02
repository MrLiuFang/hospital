package com.lion.upms.dao.user;

import com.lion.upms.entity.user.User;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:14
 */
public interface UserDaoEx {

    public List<User> find(String name, List<Long> userIds);

}
