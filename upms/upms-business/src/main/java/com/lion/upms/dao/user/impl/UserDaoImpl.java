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
}
