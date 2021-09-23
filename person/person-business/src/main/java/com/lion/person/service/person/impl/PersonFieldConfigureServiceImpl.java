package com.lion.person.service.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PersonFieldConfigureDao;
import com.lion.person.entity.enums.ConfigureType;
import com.lion.person.entity.person.PersonFieldConfigure;
import com.lion.person.service.person.PersonFieldConfigureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/23 下午3:02
 */
@Service
public class PersonFieldConfigureServiceImpl extends BaseServiceImpl<PersonFieldConfigure> implements PersonFieldConfigureService {

    @Autowired
    private PersonFieldConfigureDao personFieldConfigureDao;

    @Override
    public PersonFieldConfigure find(ConfigureType configureType) {
        return personFieldConfigureDao.findFirstByConfigureType(configureType);
    }
}
