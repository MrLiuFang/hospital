package com.lion.person.service.person.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.person.dao.person.TemporaryPersonDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.entity.person.dto.AddTemporaryPersonDto;
import com.lion.person.entity.person.dto.TemporaryPersonLeaveDto;
import com.lion.person.entity.person.dto.UpdateTemporaryPersonDto;
import com.lion.person.entity.person.vo.ListTemporaryPersonVo;
import com.lion.person.entity.person.vo.TemporaryPersonDetailsVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.RestrictedAreaService;
import com.lion.person.service.person.TemporaryPersonService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private PatientService patientService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void add(AddTemporaryPersonDto addTemporaryPersonDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        BeanUtils.copyProperties(addTemporaryPersonDto,temporaryPerson);
        assertPatientExist(temporaryPerson.getPatientId());
        temporaryPerson = save(temporaryPerson);
        restrictedAreaService.add(addTemporaryPersonDto.getRegionId(), PersonType.TEMPORARY_PERSON,temporaryPerson.getId());
        tagPostdocsExposeService.binding(temporaryPerson.getId(),addTemporaryPersonDto.getTagCode());
        redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON+temporaryPerson.getId(),temporaryPerson, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void update(UpdateTemporaryPersonDto updateTemporaryPersonDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        BeanUtils.copyProperties(updateTemporaryPersonDto,temporaryPerson);
        assertPatientExist(temporaryPerson.getPatientId());
        update(temporaryPerson);
        restrictedAreaService.add(updateTemporaryPersonDto.getRegionId(), PersonType.TEMPORARY_PERSON,temporaryPerson.getId());
        tagPostdocsExposeService.binding(temporaryPerson.getId(),updateTemporaryPersonDto.getTagCode());
        redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON+temporaryPerson.getId(),temporaryPerson, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtos) {
        if (Objects.nonNull(deleteDtos) ){
            deleteDtos.forEach(deleteDto -> {
                this.deleteById(deleteDto.getId());
                restrictedAreaService.delete(deleteDto.getId());
                tagPostdocsExposeService.unbinding(deleteDto.getId(),true);
                redisTemplate.delete(RedisConstants.TEMPORARY_PERSON+deleteDto.getId());
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
        list.forEach(temporaryPerson -> {
            ListTemporaryPersonVo vo = new ListTemporaryPersonVo();
            TemporaryPersonDetailsVo temporaryPersonDetailsVo = details(temporaryPerson.getId());
            if (Objects.nonNull(temporaryPersonDetailsVo)) {
                BeanUtils.copyProperties(temporaryPersonDetailsVo, vo);
                returnList.add(vo);
            }
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public TemporaryPersonDetailsVo details(Long id) {
        TemporaryPerson temporaryPerson = this.findById(id);
        if (Objects.isNull(temporaryPerson)) {
            return null;
        }
        TemporaryPersonDetailsVo temporaryPersonDetailsVo= new TemporaryPersonDetailsVo();
        BeanUtils.copyProperties(temporaryPerson,temporaryPersonDetailsVo);
        temporaryPersonDetailsVo.setHeadPortraitUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));

        List<RestrictedArea> restrictedAreaList = restrictedAreaService.find(temporaryPerson.getId(), PersonType.TEMPORARY_PERSON);
        List<TemporaryPersonDetailsVo.RestrictedAreaVo> restrictedAreaVoList = new ArrayList<>();
        restrictedAreaList.forEach(restrictedArea -> {
            TemporaryPersonDetailsVo.RestrictedAreaVo restrictedAreaVo = new TemporaryPersonDetailsVo.RestrictedAreaVo();
            Region region = regionExposeService.findById(restrictedArea.getRegionId());
            if (Objects.nonNull(region)){
                restrictedAreaVo.setRegionName(region.getName());
                restrictedAreaVo.setRegionId(region.getId());
                restrictedAreaVo.setRemark(region.getRemarks());
                Build build = buildExposeService.findById(region.getBuildId());
                if (Objects.nonNull(build)){
                    restrictedAreaVo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    restrictedAreaVo.setBuildFloorName(buildFloor.getName());
                }
                restrictedAreaVoList.add(restrictedAreaVo);
            }
        });
        temporaryPersonDetailsVo.setRestrictedAreaVoList(restrictedAreaVoList);

        return temporaryPersonDetailsVo;
    }

    @Override
    public void leave(TemporaryPersonLeaveDto temporaryPersonLeaveDto) {
        TemporaryPerson temporaryPerson = new TemporaryPerson();
        temporaryPerson.setId(temporaryPersonLeaveDto.getTemporaryPersonId());
        temporaryPerson.setIsLeave(true);
        temporaryPerson.setLeaveRemarks(temporaryPersonLeaveDto.getLeaveRemarks());
        temporaryPerson.setLeaveDateTime(LocalDateTime.now());
        update(temporaryPerson);
        tagPostdocsExposeService.unbinding(temporaryPerson.getId(),false);
    }

    private void assertPatientExist(Long patientId) {
        Patient patient = patientService.findById(patientId);
        if (Objects.isNull(patient)){
            BusinessException.throwException("该患者不存在");
        }
        if (!Objects.equals(patient.getIsLeave(), false)) {
            BusinessException.throwException("该患者已登出");
        }
    }
}
