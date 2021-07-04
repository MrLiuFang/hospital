package com.lion.person.expose.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/4 上午9:39
 */
public interface RestrictedAreaExposeServiceService extends BaseService<RestrictedArea> {

    /**
     * 查询限制活动区域
     * @param id
     * @param personType
     * @return
     */
    List<RestrictedArea> find(Long id, PersonType personType);
}
