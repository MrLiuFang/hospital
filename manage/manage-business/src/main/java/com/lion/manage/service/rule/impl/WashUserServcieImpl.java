package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.rule.WashUserDao;
import com.lion.manage.entity.enums.WashRuleType;
import com.lion.manage.entity.rule.WashUser;
import com.lion.manage.service.rule.WashUserServcie;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
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

    @DubboReference
    private UserExposeService userExposeService ;

    @Override
    public void add(List<Long> userId, Long washId) {
        if (Objects.nonNull(washId)){
            washUserDao.deleteByWashId(washId);
        }
        userId.forEach(id->{
            List<WashUser> list = washUserDao.find(id, WashRuleType.LOOP,washId);
            if (Objects.nonNull(list) && list.size()>0){
                User user = userExposeService.findById(id);
                BusinessException.throwException(user.getName()+"已经存在其它区域洗手规则中,多个洗手规则会造成洗手监控冲突");
            }
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
