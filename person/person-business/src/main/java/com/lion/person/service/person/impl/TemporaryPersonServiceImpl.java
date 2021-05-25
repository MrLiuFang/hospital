package com.lion.person.service.person.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.entity.person.dto.AddTemporaryPersonDto;
import com.lion.person.entity.person.dto.UpdateTemporaryPersonDto;
import com.lion.person.service.person.RestrictedAreaService;
import com.lion.person.service.person.TemporaryPersonService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
