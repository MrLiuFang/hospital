package com.lion.manage.expose.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.Wash;

import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 下午3:42
 **/
public interface WashExposeService extends BaseService<Wash> {

    /**
     * 根据区域和用户查询洗手规则
     * @param regionId
     * @param userId
     * @return
     */
    public Wash find(Long regionId, Long userId);

    /**
     * 根据区域查询洗手规则
     * @param region
     * @return
     */
    public List<Wash> find(Long regionId);
}
