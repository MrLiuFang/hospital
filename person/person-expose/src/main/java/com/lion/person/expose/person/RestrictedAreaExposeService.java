package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.person.RestrictedArea;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/6/2 上午11:43
 */
public interface RestrictedAreaExposeService extends BaseService<RestrictedArea> {

    /**
     * 查询患者限制区域
     * @param personId
     * @return
     */
    public List<RestrictedArea> find(Long personId);
}
