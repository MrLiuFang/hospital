package com.lion.person.dao.person;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.entity.enums.ConfigureType;
import com.lion.person.entity.person.PersonFieldConfigure;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/23 下午3:02
 */
public interface PersonFieldConfigureDao extends BaseDao<PersonFieldConfigure> {

    /**
     * 根据类型查询
     * @param configureType
     * @return
     */
    public PersonFieldConfigure findFirstByConfigureType(ConfigureType configureType);
}
