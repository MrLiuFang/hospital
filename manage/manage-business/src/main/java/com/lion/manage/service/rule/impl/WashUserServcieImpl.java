package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashUserDao;
import com.lion.manage.entity.rule.WashDevice;
import com.lion.manage.entity.rule.WashRegion;
import com.lion.manage.entity.rule.WashUser;
import com.lion.manage.service.rule.WashUserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:01
 */
@Service
public class WashUserServcieImpl extends BaseServiceImpl<WashUser> implements WashUserServcie {

    @Autowired
    private WashUserDao washUserDao;

    @Override
    public void add(List<Long> userId, Long washId) {
        if (Objects.nonNull(washId)){
            washUserDao.deleteByWashId(washId);
        }
        userId.forEach(id->{
            WashUser washUser = new WashUser();
            washUser.setUserId(id);
            washUser.setWashId(washId);
            save(washUser);
        });
    }

    @Override
    public int delete(Long washId) {
        return washUserDao.deleteByWashId(washId);
    }

    @Override
    public List<WashUser> find(Long washId) {
        return washUserDao.findByWashId(washId);
    }
}
