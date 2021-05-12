package com.lion.manage.expose.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.Wash;
import com.lion.manage.entity.rule.WashUser;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午9:12
 **/
public interface WashUserExposeService extends BaseService<WashUser> {

    /**
     * 根据用户查询洗手规则
     * @param userId
     * @return
     */
    public List<WashUser> find(Long userId);
}
