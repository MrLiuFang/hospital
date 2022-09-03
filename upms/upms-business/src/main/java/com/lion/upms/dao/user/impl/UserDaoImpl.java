package com.lion.upms.dao.user.impl;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.dao.user.UserDaoEx;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:16
 */
public class UserDaoImpl implements UserDaoEx {

    @Autowired
    private BaseDao<User> baseDao;

    @Override
    public List<User> find(String name, List<Long> userIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select u from User u where 1=1  ");
        Map<String, Object> searchParameter = new HashMap();
        if (Objects.nonNull(userIds) && userIds.size()>0){
            sb.append(" and u.id in :userIds ");
            searchParameter.put("userIds", userIds);
        }
        if (StringUtils.hasText(name)) {
            sb.append(" and  (u.name like :name or u.email like :email or u.tagCode like :tagCode or u.phoneNumber like :phoneNumber or u.address like :address ) ");
            searchParameter.put("name", "%"+name+"%");
            searchParameter.put("email", "%"+name+"%");
            searchParameter.put("tagCode", "%"+name+"%");
            searchParameter.put("phoneNumber", "%"+name+"%");
            searchParameter.put("address", "%"+name+"%");
        }
        return (List<User>) this.baseDao.findAll(sb.toString(),searchParameter);
    }
}
