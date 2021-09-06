package com.lion.upms.service.user;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserDto;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:54
 */
public interface UserTypeService extends BaseService<UserType> {

    /**
     * 新增
     * @param addUserTypeDto
     */
    public void add(AddUserTypeDto addUserTypeDto);

    /**
     * 修改
     * @param updateUserDto
     */
    public void update(UpdateUserDto updateUserDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

}
