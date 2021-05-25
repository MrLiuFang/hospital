package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.entity.person.dto.AddTemporaryPersonDto;
import com.lion.person.entity.person.dto.TemporaryPersonLeaveDto;
import com.lion.person.entity.person.dto.UpdateTemporaryPersonDto;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.ListTemporaryPersonVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.entity.person.vo.TemporaryPersonDetailsVo;
import com.lion.person.service.person.RestrictedAreaService;
import com.lion.person.service.person.TemporaryPersonService;
import org.apache.dubbo.config.annotation.DubboReference;
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
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:09
 */
@Service
public class TemporaryPersonServiceImpl extends BaseServiceImpl<TemporaryPerson> implements TemporaryPersonService {

    @Autowired
    private TemporaryPersonDao temporaryPersonDao;

    @Autowired
    private RestrictedAreaService restrictedAreaService;

    @DubboReference
    private TagPostdocsExposeService tagPostdocsExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    @Transactional
    public void add(AddTemporaryPersonDto addTemporaryPersonDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        BeanUtils.copyProperties(addTemporaryPersonDto,temporaryPerson);
        temporaryPerson = save(temporaryPerson);
        restrictedAreaService.add(addTemporaryPersonDto.getRegionId(), PersonType.TEMPORARY_PERSON,temporaryPerson.getId());
        tagPostdocsExposeService.binding(temporaryPerson.getId(),addTemporaryPersonDto.getTagCode());
    }

    @Override
    @Transactional
    public void update(UpdateTemporaryPersonDto updateTemporaryPersonDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        BeanUtils.copyProperties(updateTemporaryPersonDto,temporaryPerson);
        update(temporaryPerson);
        restrictedAreaService.add(updateTemporaryPersonDto.getRegionId(), PersonType.TEMPORARY_PERSON,temporaryPerson.getId());
        tagPostdocsExposeService.binding(temporaryPerson.getId(),updateTemporaryPersonDto.getTagCode());
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtos) {
        if (Objects.nonNull(deleteDtos) ){
            deleteDtos.forEach(deleteDto -> {
                this.deleteById(deleteDto.getId());
                restrictedAreaService.delete(deleteDto.getId());
                tagPostdocsExposeService.unbinding(deleteDto.getId(),true);
            });
        }
    }

    @Override
    public IPageResultData<List<ListTemporaryPersonVo>> list(String name, Boolean isLeave, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (Objects.nonNull(isLeave)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_isLeave",isLeave);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<TemporaryPerson> page = this.findNavigator(lionPage);
        List<TemporaryPerson> list = page.getContent();
        List<ListTemporaryPersonVo> returnList = new ArrayList<>();
        list.forEach(patient -> {
            ListTemporaryPersonVo vo = new ListTemporaryPersonVo();
            TemporaryPersonDetailsVo temporaryPersonDetailsVo = details(patient.getId());
            BeanUtils.copyProperties(temporaryPersonDetailsVo,vo);
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public TemporaryPersonDetailsVo details(Long id) {
        TemporaryPerson temporaryPerson = this.findById(id);
        if (Objects.nonNull(temporaryPerson)) {
            return null;
        }
        TemporaryPersonDetailsVo temporaryPersonDetailsVo= new TemporaryPersonDetailsVo();
        BeanUtils.copyProperties(temporaryPerson,temporaryPersonDetailsVo);
        temporaryPersonDetailsVo.setHeadPortraitUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
        return temporaryPersonDetailsVo;
    }

    @Override
    public void leave(TemporaryPersonLeaveDto temporaryPersonLeaveDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        temporaryPerson.setId(temporaryPersonLeaveDto.getTemporaryPersonId());
        temporaryPerson.setIsLeave(true);
        temporaryPerson.setLeaveRemarks(temporaryPersonLeaveDto.getLeaveRemarks());
        update(temporaryPerson);
    }
}
