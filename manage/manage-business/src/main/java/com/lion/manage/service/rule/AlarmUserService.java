package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.AlarmUser;

import java.util.List;

/**
 * @author Mr.Liu
 * @classname AlarmUserService
 * @description
 * @date 2022/04/10 上午10:47
 */
public interface AlarmUserService extends BaseService<AlarmUser> {

    public void add(Long alarmId, List<Long> userId);
}
