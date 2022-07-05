package com.lion.device.service.device.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.WarningBellDao;
import com.lion.device.entity.device.QWarningBell;
import com.lion.device.entity.device.WarningBell;
import com.lion.device.entity.device.dto.AddWarningBellDto;
import com.lion.device.entity.device.dto.UpdateWarningBellDto;
import com.lion.device.entity.device.vo.DetailsWarningBellVo;
import com.lion.device.entity.device.vo.ListWarningBellVo;
import com.lion.device.service.device.WarningBellService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.region.RegionWarningBell;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.manage.expose.region.RegionWarningBellExposeService;
import com.lion.utils.MessageI18nUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.dubbo.config.annotation.DubboReference;
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
 * @createDateTime 2021/9/8 下午1:57
 */
@Service
public class WarningBellServiceImpl extends BaseServiceImpl<WarningBell> implements WarningBellService {

    @Autowired
    private WarningBellDao warningBellDao;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @DubboReference
    private RegionWarningBellExposeService regionWarningBellExposeService;

    @Override
    public void add(AddWarningBellDto addWarningBellDto) {
        WarningBell warningBell = new WarningBell();
        BeanUtils.copyProperties(addWarningBellDto,warningBell);
        assertNameExist(warningBell.getName(),null);
        assertCodeExist(warningBell.getCode(),null);
        assertWarningBellIdExist(warningBell.getWarningBellId(),null);
        save(warningBell);
    }

    @Override
    public void update(UpdateWarningBellDto updateWarningBellDto) {
        WarningBell warningBell = new WarningBell();
        BeanUtils.copyProperties(updateWarningBellDto,warningBell);
        assertNameExist(warningBell.getName(),warningBell.getId());
        assertCodeExist(warningBell.getCode(),warningBell.getId());
        assertWarningBellIdExist(warningBell.getWarningBellId(),warningBell.getId());
        update(warningBell);
    }

    @Override
    public void delete(List<DeleteDto> deleteDto) {
        deleteDto.forEach(dto ->{
            this.deleteById(dto.getId());
        });
    }

    @Override
    public IPageResultData<List<ListWarningBellVo>> list(Boolean isBindRegion, String name, String code, String warningBellId, Long departmentId, LionPage LionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_name", name);
        }
        if (Objects.equals(isBindRegion,false)) {
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            ids.addAll(regionWarningBellExposeService.findAllBindId());
            jpqlParameter.setSearchParameter(SearchConstant.NOT_IN + "_id", ids);
        }
        LionPage.setJpqlParameter(jpqlParameter);
        Page<WarningBell> page = this.findNavigator(LionPage);
        List<WarningBell> list = page.getContent();
        List<ListWarningBellVo> returnList = new ArrayList<ListWarningBellVo>();
        list.forEach(w -> {
            ListWarningBellVo vo = new ListWarningBellVo();
            BeanUtils.copyProperties(w, vo);
            returnList.add(vo);
        });
        return new PageResultData<List<ListWarningBellVo>>(returnList,LionPage,page.getTotalElements());
    }

    @Override
    public DetailsWarningBellVo details(Long id) {
        com.lion.core.Optional<WarningBell> optionalWarningBell = this.findById(id);
        if (optionalWarningBell.isPresent()){
            WarningBell warningBell = optionalWarningBell.get();
            DetailsWarningBellVo detailsWarningBellVo = new DetailsWarningBellVo();
            BeanUtils.copyProperties(warningBell,detailsWarningBellVo);
            if (Objects.nonNull(warningBell.getDepartmentId())){
                com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(warningBell.getDepartmentId());
                if (optionalDepartment.isPresent()) {
                    detailsWarningBellVo.setDepartmentName(optionalDepartment.get().getName());
                }
            }
            RegionWarningBell regionWarningBell = regionWarningBellExposeService.find(warningBell.getId());
            if (Objects.nonNull(regionWarningBell)) {
                com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(regionWarningBell.getRegionId());
                if (optionalRegion.isPresent()) {
                    detailsWarningBellVo.setRegionName(optionalRegion.get().getName());
                }
            }

            detailsWarningBellVo.setImgUrl(fileExposeService.getUrl(warningBell.getImg()));
            return detailsWarningBellVo;
        }
        return null;
    }

    private void assertNameExist(String name, Long id) {
        QWarningBell qWarningBell = QWarningBell.warningBell;
        WarningBell warningBell = jpaQueryFactory.selectFrom(qWarningBell).where(qWarningBell.name.eq(name)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(warningBell)) || (Objects.nonNull(id) && Objects.nonNull(warningBell) && !Objects.equals(warningBell.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000114",new Object[]{name}));
        }
    }

    private void assertCodeExist(String code, Long id) {
        QWarningBell qWarningBell = QWarningBell.warningBell;
        WarningBell warningBell = jpaQueryFactory.selectFrom(qWarningBell).where(qWarningBell.code.eq(code)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(warningBell)) || (Objects.nonNull(id) && Objects.nonNull(warningBell) && !Objects.equals(warningBell.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000115",new Object[]{code}));
        }
    }

    private void assertWarningBellIdExist(String warningBellId, Long id) {
        QWarningBell qWarningBell = QWarningBell.warningBell;
        WarningBell warningBell = jpaQueryFactory.selectFrom(qWarningBell).where(qWarningBell.warningBellId.eq(warningBellId)).fetchOne();
        if ((Objects.isNull(id) && Objects.nonNull(warningBell)) || (Objects.nonNull(id) && Objects.nonNull(warningBell) && !Objects.equals(warningBell.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000116",new Object[]{warningBellId}));
        }
    }
}
