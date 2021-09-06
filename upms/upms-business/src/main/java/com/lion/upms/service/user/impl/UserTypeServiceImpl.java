package com.lion.upms.service.user.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserDto;
import com.lion.upms.service.user.UserService;
import com.lion.upms.service.user.UserTypeService;
import com.lion.utils.AssertUtil;
import com.lion.utils.MessageI18nUtil;
import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:54
 */
@Service
public class UserTypeServiceImpl extends BaseServiceImpl<UserType> implements UserTypeService {

    @Autowired
    private UserDao userDao;

    @Override
    public void add(AddUserTypeDto addUserTypeDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(addUserTypeDto,userType);
        this.save(userType);
    }

    @Override
    public void update(UpdateUserDto updateUserDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(updateUserDto,userType);
        this.update(userType);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto -> {
            UserType userType = this.findById(dto.getId());
            if (Objects.nonNull(userType)) {
                AssertUtil.isTrue(userDao.countByUserTypeId(dto.getId()) > 0, MessageI18nUtil.getMessage("0000023",new Object[]{userType.getName()}) );
            }
        });
        deleteDto.forEach(dto -> {
            this.deleteById(dto.getId());
        });
    }
}
