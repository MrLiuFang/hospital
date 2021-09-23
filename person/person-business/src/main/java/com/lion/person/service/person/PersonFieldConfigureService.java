package com.lion.person.service.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.ConfigureType;
import com.lion.person.entity.person.PersonFieldConfigure;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/23 下午3:02
 */
public interface PersonFieldConfigureService extends BaseService<PersonFieldConfigure> {

    /**
     * 根据类型查询
     * @param configureType
     * @return
     */
    public PersonFieldConfigure find(ConfigureType configureType);
}
