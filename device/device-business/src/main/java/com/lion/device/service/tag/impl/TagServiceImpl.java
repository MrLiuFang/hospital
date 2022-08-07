package com.lion.device.service.tag.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.*;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.*;
import com.lion.device.entity.tag.dto.AddTagDto;
import com.lion.device.entity.tag.dto.UpdateTagDto;
import com.lion.device.entity.tag.vo.DetailsTagVo;
import com.lion.device.entity.tag.vo.ListTagVo;
import com.lion.device.entity.tag.vo.PurposeStatisticsVo;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.device.service.tag.TagService;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.event.expose.service.HumitureRecordExposeService;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.expose.person.PatientExposeService;
import com.lion.person.expose.person.TemporaryPersonExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午8:16
 */
@Service
public class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagAssetsDao tagAssetsDao;

    @Autowired
    private TagUserDao tagUserDao;

    @Autowired
    private TagPatientDao tagPatientDao;

    @Autowired
    private TagPostdocsDao tagPostdocsDao;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private PatientExposeService patientExposeService;

    @DubboReference
    private TemporaryPersonExposeService temporaryPersonExposeService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private TagUserExposeService tagUserExposeService;

    @DubboReference
    private TagPatientExposeService tagPatientExposeService;

    @DubboReference
    private TagPostdocsExposeService tagPostdocsExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @DubboReference
    private HumitureRecordExposeService humitureRecordExposeService;

    @DubboReference
    private SystemAlarmExposeService systemAlarmExposeService;

    @Override
    public void add(AddTagDto addTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(addTagDto,tag);
        assertDepartmentExist(addTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),null);
        assertDeviceNameExist(tag.getDeviceName(),null);
        assertTagCodeExist(tag.getTagCode(),null);
        assertTagPurpose(tag);
        tag = save(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag,5, TimeUnit.MINUTES);
    }

    @Override
    public void update(UpdateTagDto updateTagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(updateTagDto,tag);
        assertDepartmentExist(updateTagDto.getDepartmentId());
        assertDeviceCodeExist(tag.getDeviceCode(),tag.getId());
        assertDeviceNameExist(tag.getDeviceName(),tag.getId());
        assertTagCodeExist(tag.getTagCode(),tag.getId());
        assertTagPurpose(tag);
        update(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, 5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Tag> delete(List<DeleteDto> deleteDtoList) {
        List<Tag> list = new ArrayList<>();
        deleteDtoList.forEach(deleteDto -> {
            TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                com.lion.core.Optional<Tag> optional = findById(tagUser.getTagId());
                if (optional.isPresent()) {
                    BusinessException.throwException(optional.get().getTagCode() + MessageI18nUtil.getMessage("4000044"));
                }
            }
            TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                com.lion.core.Optional<Tag> optional = findById(tagAssets.getTagId());
                if (optional.isPresent()) {
                    BusinessException.throwException(optional.get().getTagCode() + MessageI18nUtil.getMessage("4000045"));
                }
            }
            TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPatient)) {
                com.lion.core.Optional<Tag> optional = findById(tagPatient.getTagId());
                if (optional.isPresent()) {
                    BusinessException.throwException(optional.get().getTagCode() + MessageI18nUtil.getMessage("4000046"));
                }
            }
            TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPostdocs)) {
                com.lion.core.Optional<Tag> optional = findById(tagPostdocs.getTagId());
                if (optional.isPresent()) {
                    BusinessException.throwException(optional.get().getTagCode() + MessageI18nUtil.getMessage("4000047"));
                }
            }
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE+deleteDto.getId());
            currentPositionExposeService.delete(null,null,deleteDto.getId());
        });

        deleteDtoList.forEach(deleteDto -> {
            Optional<Tag> optional = findById(deleteDto.getId());
            TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            this.deleteById(deleteDto.getId());
            tagAssetsDao.deleteByTagId(deleteDto.getId());
            tagPatientDao.deleteByTagId(deleteDto.getId());
            tagPostdocsDao.deleteByTagId(deleteDto.getId());
            tagUserDao.deleteByTagId(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                redisTemplate.delete(RedisConstants.USER_TAG + tagUser.getUserId());
                redisTemplate.delete(RedisConstants.TAG_USER + tagUser.getTagId());
            }
            if (optional.isPresent()){
                Tag tag = optional.get();
                Tag newTag = new Tag();
                newTag.setTagCode(tag.getTagCode());
                newTag.setDeviceState(State.NOT_ACTIVE);
                newTag.setType(tag.getType());
                list.add(newTag);
//                save(newTag);
            }
        });
//        saveNewTag(list);
        return list;
    }
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void del(Tag tag) {
//        this.delete(tag);
//    }


//    public void saveNewTag(List<Tag> list) {
//        list.forEach(tag -> {
//            save(tag);
//        });
//    }

    @Override
    public IPageResultData<List<ListTagVo>> list(Boolean isTmp, Long departmentId, TagUseState useState, State state, Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.equals(true,isTmp)){
            jpqlParameter.setSearchParameter(SearchConstant.IS_NULL+"_departmentId",null);
            jpqlParameter.setSearchParameter(SearchConstant.IS_NULL+"_purpose",null);
        }else {
            jpqlParameter.setSearchParameter(SearchConstant.IS_NOT_NULL+"_departmentId",null);
            jpqlParameter.setSearchParameter(SearchConstant.IS_NOT_NULL+"_purpose",null);
        }
        List<Long> departmentIds = new ArrayList<>();
        departmentIds = departmentExposeService.responsibleDepartment(departmentId);
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_tagCode",tagCode);
        }
//        if (Objects.nonNull(departmentId)){
//            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
//        }
        if (departmentIds.size()>0 && !Objects.equals(true,isTmp)) {
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",departmentIds);
        }
        if (Objects.nonNull(type)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (Objects.nonNull(purpose)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_purpose",purpose);
        }
        if (Objects.nonNull(battery)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_battery",battery);
        }
        if (Objects.nonNull(state)){
            if (Objects.equals(state,State.NOT_USED)) {
                List<State> list = new ArrayList<>();
                list.add(State.ACTIVE);
                list.add(State.NOT_USED);
                jpqlParameter.setSearchParameter(SearchConstant.IN + "_deviceState", list);
            }else {
                jpqlParameter.setSearchParameter(SearchConstant.EQUAL + "_deviceState", state);
            }
        }
        if (Objects.nonNull(useState)){
            if (Objects.equals(useState,TagUseState.NOT_USED)) {
                List<State> list = new ArrayList<>();
                list.add(State.ACTIVE);
                list.add(State.NOT_USED);
                jpqlParameter.setSearchParameter(SearchConstant.IN + "_deviceState", list);
            }
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Tag> page = findNavigator(lionPage);
        List<Tag> list = page.getContent();
        List<ListTagVo> returnList = new ArrayList<>();
        list.forEach(tag->{
            ListTagVo vo = new ListTagVo();
            BeanUtils.copyProperties(tag,vo);
            if (Objects.nonNull(tag.getDepartmentId())) {
                com.lion.core.Optional<Department> optional = departmentExposeService.findById(tag.getDepartmentId());
                if (optional.isPresent()){
                    vo.setDepartmentName(optional.get().getName());
                }
            }
            if (Objects.equals(tag.getPurpose(),TagPurpose.STAFF)) {
                TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagUser)) {
                    com.lion.core.Optional<User> optional = userExposeService.findById(tagUser.getUserId());
                    if (optional.isPresent()) {
                        User user = optional.get();
                        vo.setBindingName(user.getName()+":"+user.getNumber());
                        vo.setBindingId(user.getId());
//                        Department department = departmentUserExposeService.findDepartment(user.getId());
//                        if (Objects.nonNull(department)) {
//                            vo.setDepartmentName(department.getName());
//                        }
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.PATIENT)) {
                TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagPatient)) {
                    com.lion.core.Optional<Patient> optional = patientExposeService.findById(tagPatient.getPatientId());
                    if (optional.isPresent()) {
                        Patient patient = optional.get();
                        vo.setBindingName(patient.getName());
                        vo.setBindingId(patient.getId());
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.POSTDOCS)) {
                TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagPostdocs)) {
                    com.lion.core.Optional<TemporaryPerson> optional = temporaryPersonExposeService.findById(tagPostdocs.getPostdocsId());
                    if (optional.isPresent()) {
                        TemporaryPerson temporaryPerson = optional.get();
                        vo.setBindingName(temporaryPerson.getName());
                        vo.setBindingId(temporaryPerson.getId());
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.ASSETS)) {
                TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagAssets)){
                    com.lion.core.Optional<Assets> optional = assetsExposeService.findById(tagAssets.getAssetsId());
                    if (optional.isPresent()){
                        Assets assets = optional.get();
                        vo.setBindingName(assets.getName());
                        vo.setBindingId(assets.getId());
                    }
                }
            }
            if (Objects.nonNull(vo.getBindingId())) {
                if (!Objects.equals(tag.getDeviceState(),State.USED)) {
                    tag.setDeviceState(State.USED);
                    update(tag);
                    vo.setDeviceState(State.USED);
                }
            }
            returnList.add(vo);
        });
        return new PageResultData<List<ListTagVo>>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public List<Long> allId() {
        return tagDao.allId();
    }

    @Override
    public DetailsTagVo details(Long id) {
        com.lion.core.Optional<Tag> optional = findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Tag tag = optional.get();
        DetailsTagVo vo = new DetailsTagVo();
        BeanUtils.copyProperties(tag,vo);
        TagUser tagUser = tagUserExposeService.find(tag.getId());
        if (Objects.nonNull(tagUser)){
            com.lion.core.Optional<User> optionalUser = userExposeService.findById(tagUser.getUserId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                vo.setBindingName(user.getName());
                vo.setImg(user.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                vo.setBindingId(user.getId());
            }
            return vo;
        }
        TagAssets tagAssets = tagAssetsExposeService.findByTagId(tag.getId());
        if (Objects.nonNull(tagAssets)) {
            com.lion.core.Optional<Assets> optionalAssets = assetsExposeService.findById(tagAssets.getAssetsId());
            if (optionalAssets.isPresent()) {
                Assets assets = optionalAssets.get();
                vo.setBindingName(assets.getName());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setBindingId(assets.getId());
            }
            return vo;
        }
        TagPatient tagPatient = tagPatientExposeService.find(tag.getId());
        if (Objects.nonNull(tagPatient)) {
            com.lion.core.Optional<Patient> optionalPatient = patientExposeService.findById(tagPatient.getPatientId());
            if (optionalPatient.isPresent()) {
                Patient patient = optionalPatient.get();
                vo.setBindingName(patient.getName());
                vo.setImg(patient.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                vo.setBindingId(patient.getId());
            }
            return vo;
        }
        TagPostdocs tagPostdocs = tagPostdocsExposeService.find(tag.getId());
        if (Objects.nonNull(tagPostdocs)) {
            com.lion.core.Optional<TemporaryPerson> optionalTemporaryPerson = temporaryPersonExposeService.findById(tagPostdocs.getPostdocsId());
            if (optionalTemporaryPerson.isPresent()) {
                TemporaryPerson temporaryPerson = optionalTemporaryPerson.get();
                vo.setBindingName(temporaryPerson.getName());
                vo.setImg(temporaryPerson.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                vo.setBindingId(temporaryPerson.getId());
            }
            return vo;
        }
        com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(tag.getDepartmentId());
        if (optionalDepartment.isPresent()) {
            vo.setDepartmentName(optionalDepartment.get().getName());
        }
        if (Objects.equals(tag.getPurpose(),TagPurpose.THERMOHYGROGRAPH)) {
            HumitureRecord humitureRecord = humitureRecordExposeService.findLast(tag.getId());
            if (Objects.nonNull(humitureRecord)) {
                vo.setHumidity(humitureRecord.getH());
                vo.setTemperature(humitureRecord.getT());
                vo.setDateTime(humitureRecord.getDdt());
            }
        }
        SystemAlarm systemAlarm =  systemAlarmExposeService.findLastByAssetsId(tag.getId());
        if (Objects.nonNull(systemAlarm)) {
            vo.setAlarm(systemAlarmExposeService.getSystemAlarmTypeDesc(systemAlarm.getSat()));
            vo.setAlarmType(systemAlarmExposeService.getSystemAlarmTypeCode(systemAlarm.getSat()));
            vo.setAlarmDataTime(systemAlarm.getDt());
            vo.setAlarmId(systemAlarm.get_id());
        }
        List<HumitureRecord> humitureRecordList =  humitureRecordExposeService.find(tag.getId(), LocalDateTime.now().minusHours(24),LocalDateTime.now());
        List<DetailsTagVo.Temperature24hour> temperature24hour = new ArrayList<>();
        List<DetailsTagVo.Humidity24hour> humidity24hour = new ArrayList<>();
        HashMap<LocalTime,DetailsTagVo.Temperature24hour> ht = new HashMap<LocalTime,DetailsTagVo.Temperature24hour>();
        HashMap<LocalTime,DetailsTagVo.Humidity24hour> hh = new HashMap<LocalTime,DetailsTagVo.Humidity24hour>();
        humitureRecordList.forEach(humitureRecord -> {
            LocalTime time = LocalTime.of(humitureRecord.getDdt().getHour(),0);
            if (Objects.nonNull(humitureRecord.getH())) {
                if (hh.containsKey(time)) {
                    DetailsTagVo.Humidity24hour h = hh.get(time);
                    if (humitureRecord.getH().compareTo(h.getHumidity())==1) {
                        h.setHumidity(humitureRecord.getH());
                    }
                }else {
                    DetailsTagVo.Humidity24hour h = DetailsTagVo.Humidity24hour.builder()
                            .time(time)
                            .humidity(humitureRecord.getH())
                            .build();
                    hh.put(time,h);
                }
            }

            if (Objects.nonNull(humitureRecord.getT())) {
                if (ht.containsKey(time)) {
                    DetailsTagVo.Temperature24hour t = ht.get(time);
                    if (humitureRecord.getT().compareTo(t.getTemperature())==1) {
                        t.setTemperature(humitureRecord.getT());
                    }
                }else {
                    DetailsTagVo.Temperature24hour t = DetailsTagVo.Temperature24hour.builder()
                            .time(time)
                            .temperature(humitureRecord.getT())
                            .build();
                    ht.put(time,t);
                }
            }
        });
        ht.forEach((k,v)->{
            temperature24hour.add(v);
        });
        hh.forEach((k,v)->{
            humidity24hour.add(v);
        });
        vo.setTemperature24hour(temperature24hour);
        vo.setHumidity24hour(humidity24hour);

        return vo;
    }

    @Override
    public List<PurposeStatisticsVo> purposeStatistics() {
        List<PurposeStatisticsVo> list = new ArrayList<PurposeStatisticsVo>();
        for (TagPurpose tagPurpose: TagPurpose.values()){
            int count = tagDao.countByPurpose(tagPurpose);
            PurposeStatisticsVo vo = PurposeStatisticsVo.builder()
                    .purpose(tagPurpose)
                    .count(count)
                    .build();
            list.add(vo);
        }
        return list;
    }

    private void assertDeviceCodeExist(String deviceCode, Long id) {
        if (StringUtils.hasText(deviceCode)) {
            Tag tag = tagDao.findFirstByDeviceCode(deviceCode);
            if ((Objects.isNull(id) && Objects.nonNull(tag)) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(), id))) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000048"));
            }
        }
    }

    private void assertDeviceNameExist(String deviceName, Long id) {
        if (StringUtils.hasText(deviceName)) {
            Tag tag = tagDao.findFirstByDeviceName(deviceName);
            if ((Objects.isNull(id) && Objects.nonNull(tag)) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(), id))) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000049"));
            }
        }
    }

    private void assertTagCodeExist(String tagCode, Long id) {
        if (StringUtils.hasText(tagCode)) {
            Tag tag = tagDao.findFirstByTagCode(tagCode);
            if ((Objects.isNull(id) && Objects.nonNull(tag)) || (Objects.nonNull(id) && Objects.nonNull(tag) && !Objects.equals(tag.getId(), id))) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000050"));
            }
        }
    }

    private void assertDepartmentExist(Long departmentId) {
        com.lion.core.Optional<Department> optional = departmentExposeService.findById(departmentId);
        if (optional.isEmpty() ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000069"));
        }
    }

    private void assertTagPurpose(Tag tag) {
        if (Objects.equals(tag.getType(), TagType.STAFF)) {
            if (!Objects.equals(tag.getPurpose(), TagPurpose.STAFF)) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000051"));
            }
        }

        if (Objects.equals(tag.getType(), TagType.TEMPERATURE_HUMIDITY)) {
            if (!Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH)) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000052"));
            }
        }
    }
}
