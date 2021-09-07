package com.lion.manage.service.assets.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.dao.assets.AssetsTypeDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.QAssetsType;
import com.lion.manage.entity.assets.dto.AddAssetsTypeDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsTypeDto;
import com.lion.manage.entity.assets.vo.ListAssetsTypeVo;
import com.lion.manage.service.assets.AssetsTypeService;
import com.lion.upms.entity.user.QUserType;
import com.lion.upms.entity.user.UserType;
import com.lion.upms.entity.user.vo.ListUserTypeVo;
import com.lion.utils.AssertUtil;
import com.lion.utils.MessageI18nUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午9:34
 */
@Service
public class AssetsTypeServiceImpl extends BaseServiceImpl<AssetsType> implements AssetsTypeService {

    @Autowired
    private AssetsTypeDao assetsTypeDao;

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public void add(AddAssetsTypeDto addAssetsTypeDto) {
        AssetsType assetsType = new AssetsType();
        BeanUtils.copyProperties(addAssetsTypeDto,assetsType);
        assertAssetsTypeNameExist(assetsType.getAssetsTypeName(),null);
        save(assetsType);
    }

    @Override
    public void update(UpdateAssetsTypeDto updateAssetsTypeDto) {
        AssetsType assetsType = new AssetsType();
        BeanUtils.copyProperties(updateAssetsTypeDto,assetsType);
        assertAssetsTypeNameExist(assetsType.getAssetsTypeName(),assetsType.getId());
        save(assetsType);
    }

    @Override
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto -> {
            AssetsType assetsType = this.findById(dto.getId());
            if (Objects.nonNull(assetsType)) {
                AssertUtil.isTrue(assetsDao.countByAssetsTypeId(dto.getId()) > 0, MessageI18nUtil.getMessage("2000101",new Object[]{assetsType.getAssetsTypeName()}) );
            }
        });
        deleteDto.forEach(dto -> {
            this.deleteById(dto.getId());
        });
    }

    @Override
    public IPageResultData<List<ListAssetsTypeVo>> list(String name, LionPage LionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_userTypeName", name);
        }
        LionPage.setJpqlParameter(jpqlParameter);
        Page<AssetsType> page = this.findNavigator(LionPage);
        List<AssetsType> list = page.getContent();
        List<ListAssetsTypeVo> returnList = new ArrayList<ListAssetsTypeVo>();
        list.forEach(assetsType -> {
            ListAssetsTypeVo vo = new ListAssetsTypeVo();
            BeanUtils.copyProperties(assetsType, vo);
            vo.setAssetsCount(assetsDao.countByAssetsTypeId(assetsType.getId()));
            returnList.add(vo);
        });
        return new PageResultData<List<ListAssetsTypeVo>>(returnList,LionPage,page.getTotalElements());
    }

    private void assertAssetsTypeNameExist(String assetsTypeName, Long id) {
        QAssetsType qAssetsType = QAssetsType.assetsType;
        AssetsType assetsType = jpaQueryFactory.selectFrom(qAssetsType).where(qAssetsType.assetsTypeName.eq(assetsTypeName)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(assetsType)) || (Objects.nonNull(id) && Objects.nonNull(assetsType) && !Objects.equals(assetsType.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000100",new Object[]{assetsTypeName}));
        }
    }
}
