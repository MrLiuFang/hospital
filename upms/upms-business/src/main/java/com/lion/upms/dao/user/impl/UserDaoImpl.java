package com.lion.upms.dao.user.impl;

import cn.hutool.core.util.NumberUtil;
import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.upms.dao.user.UserDaoEx;
import com.lion.upms.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/24下午2:16
 */
public class UserDaoImpl implements UserDaoEx {

    @Autowired
    private BaseDao<User> baseDao;

    public Page<User> list(String keyword, LionPage lionPage){
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select u from User u where 1=1 ");
        if (StringUtils.hasText(keyword)){
            sb.append(" or u.name like :name");
            searchParameter.put("name","%"+keyword+"%");

            sb.append(" or u.username like :username");
            searchParameter.put("username","%"+keyword+"%");

            sb.append(" or u.email like :email");
            searchParameter.put("email","%"+keyword+"%");

            sb.append(" or u.tagCode like :tagCode");
            searchParameter.put("tagCode","%"+keyword+"%");

            sb.append(" or u.address like :address");
            searchParameter.put("address","%"+keyword+"%");

            sb.append(" or u.address like :address");
            searchParameter.put("address","%"+keyword+"%");
        }

        if (NumberUtil.isInteger(keyword)){
            sb.append(" or u.number = :number");
            searchParameter.put("number",Integer.valueOf(keyword));
        }

        Page page = baseDao.findNavigator(lionPage, sb.toString(), searchParameter);
        return page;
    }
}
