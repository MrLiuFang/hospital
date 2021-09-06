package com.lion.upms.service.user.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserTypeDto;
import com.lion.upms.entity.user.vo.ListUserTypeVo;
import com.lion.upms.service.user.UserTypeService;
import com.lion.utils.AssertUtil;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    public void update(UpdateUserTypeDto updateUserTypeDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(updateUserTypeDto,userType);
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

    @Override
    public IPageResultData<List<ListUserTypeVo>> list(String name, LionPage LionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_name", name);
        }
        LionPage.setJpqlParameter(jpqlParameter);
        Page<UserType> page = this.findNavigator(LionPage);
        List<UserType> list = page.getContent();
        List<ListUserTypeVo> returnList = new ArrayList<ListUserTypeVo>();
        list.forEach(userType -> {
            ListUserTypeVo vo = new ListUserTypeVo();
            BeanUtils.copyProperties(userType, vo);
            vo.setUserCount(userDao.countByUserTypeId(userType.getId()));
            returnList.add(vo);
        });
        return new PageResultData<List<ListUserTypeVo>>(returnList,LionPage,page.getTotalElements());
    }
}
