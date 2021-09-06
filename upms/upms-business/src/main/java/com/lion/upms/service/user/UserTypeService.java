package com.lion.upms.service.user;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserTypeDto;
import com.lion.upms.entity.user.vo.ListUserTypeVo;

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
     * @param updateUserTypeDto
     */
    public void update(UpdateUserTypeDto updateUserTypeDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

    /**
     * 列表
     * @param name
     * @param LionPage
     * @return
     */
    public IPageResultData<List<ListUserTypeVo>> list(String name, LionPage LionPage);

}
