package com.lion.manage.service.region.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionTypeDao;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.QAssetsType;
import com.lion.manage.entity.assets.vo.ListAssetsTypeVo;
import com.lion.manage.entity.region.QRegionType;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.region.dto.AddRegionTypeDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.ListRegionTypeVo;
import com.lion.manage.service.region.RegionTypeService;
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
 * @createDateTime 2021/9/8 上午8:51
 */
@Service
public class RegionTypeServiceImpl extends BaseServiceImpl<RegionType> implements RegionTypeService {

    @Autowired
    private RegionTypeDao regionTypeDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public void add(AddRegionTypeDto addRegionTypeDto) {
        RegionType regionType = new RegionType();
        BeanUtils.copyProperties(addRegionTypeDto,regionType);
        assertRegionTypeNameExist(regionType.getRegionTypeName(),null);
        save(regionType);
    }

    @Override
    public void update(UpdateRegionDto updateRegionDto) {
        RegionType regionType = new RegionType();
        BeanUtils.copyProperties(updateRegionDto,regionType);
        assertRegionTypeNameExist(regionType.getRegionTypeName(),regionType.getId());
        update(regionType);
    }

    @Override
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto -> {
            RegionType regionType = this.findById(dto.getId());
            if (Objects.nonNull(regionType)) {
                AssertUtil.isTrue(regionDao.countByRegionTypeId(dto.getId()) > 0, MessageI18nUtil.getMessage("2000107",new Object[]{regionType.getRegionTypeName()}) );
            }
        });
        deleteDto.forEach(dto -> {
            this.deleteById(dto.getId());
        });
    }

    @Override
    public IPageResultData<List<ListRegionTypeVo>> list(String name, LionPage LionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_regionTypeName", name);
        }
        LionPage.setJpqlParameter(jpqlParameter);
        Page<RegionType> page = this.findNavigator(LionPage);
        List<RegionType> list = page.getContent();
        List<ListRegionTypeVo> returnList = new ArrayList<ListRegionTypeVo>();
        list.forEach(regionType -> {
            ListRegionTypeVo vo = new ListRegionTypeVo();
            BeanUtils.copyProperties(regionType, vo);
            returnList.add(vo);
        });
        return new PageResultData<List<ListRegionTypeVo>>(returnList,LionPage,page.getTotalElements());
    }

    private void assertRegionTypeNameExist(String regionTypeName, Long id) {
        QRegionType qAssetsType = QRegionType.regionType;
        RegionType regionType = jpaQueryFactory.selectFrom(qAssetsType).where(qAssetsType.regionTypeName.eq(regionTypeName)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(regionType)) || (Objects.nonNull(id) && Objects.nonNull(regionType) && !Objects.equals(regionType.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000106",new Object[]{regionTypeName}));
        }
    }
}
