package com.lion.upms.service.user.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.upms.dao.user.UserDao;
import com.lion.upms.dao.user.UserTypeDao;
import com.lion.upms.entity.user.QUserType;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserTypeDto;
import com.lion.upms.entity.user.vo.ListUserTypeVo;
import com.lion.upms.service.user.UserTypeService;
import com.lion.utils.AssertUtil;
import com.lion.utils.MessageI18nUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/6 上午10:54
 */
@Service
public class UserTypeServiceImpl extends BaseServiceImpl<UserType> implements UserTypeService {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private UserDao userDao;

    @Override
    public void add(AddUserTypeDto addUserTypeDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(addUserTypeDto,userType);
        assertUserTypeNameExist(userType.getUserTypeName(),null);
        this.save(userType);
    }

    @Override
    public void update(UpdateUserTypeDto updateUserTypeDto) {
        UserType userType = new UserType();
        BeanUtils.copyProperties(updateUserTypeDto,userType);
        assertUserTypeNameExist(userType.getUserTypeName(),userType.getId());
        this.update(userType);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto -> {
            com.lion.core.Optional<UserType> optional = this.findById(dto.getId());
            if (optional.isPresent()) {
                AssertUtil.isTrue(userDao.countByUserTypeId(dto.getId()) > 0, MessageI18nUtil.getMessage("0000023",new Object[]{optional.get().getUserTypeName()}) );
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
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_userTypeName", name);
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

    @Override
    public Optional<UserType> find(String name) {
        QUserType qUserType = QUserType.userType;
        UserType userType = jpaQueryFactory.selectFrom(qUserType).where(qUserType.userTypeName.eq(name)).fetchOne();
        return Optional.ofNullable(userType);
    }

    private void assertUserTypeNameExist(String userTypeName, Long id) {
        QUserType qUserType = QUserType.userType;
        UserType userType = jpaQueryFactory.selectFrom(qUserType).where(qUserType.userTypeName.eq(userTypeName)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(userType)) || (Objects.nonNull(id) && Objects.nonNull(userType) && !Objects.equals(userType.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("0000024",new Object[]{userTypeName}));
        }
    }
}
