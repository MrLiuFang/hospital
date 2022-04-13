package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.AlarmUserDao;
import com.lion.manage.entity.rule.AlarmUser;
import com.lion.manage.service.rule.AlarmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @classname AlarmUserServiceImpl
 * @description
 * @date 2022/04/10 上午10:47
 */
@Service
public class AlarmUserServiceImpl extends BaseServiceImpl<AlarmUser> implements AlarmUserService {

    @Autowired
    private AlarmUserDao alarmUserDao;

    @Override
    public void add(Long alarmId, List<Long> userId) {
        alarmUserDao.deleteByAlarmId(alarmId);
        if (Objects.nonNull(userId) && userId.size()>0) {
            userId.forEach(id -> {
                AlarmUser alarmUser = new AlarmUser();
                alarmUser.setAlarmId(alarmId);
                alarmUser.setUserId(id);
                save(alarmUser);
            });
        }
    }
}
