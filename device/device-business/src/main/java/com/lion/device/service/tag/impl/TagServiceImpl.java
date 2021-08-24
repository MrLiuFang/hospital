package com.lion.device.service.tag.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.*;
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
import com.lion.manage.entity.enums.SystemAlarmType;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
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
        redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {

        deleteDtoList.forEach(deleteDto -> {
            TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                Tag tag = findById(tagUser.getTagId());
                BusinessException.throwException(tag.getTagCode() + MessageI18nUtil.getMessage("4000044"));
            }
            TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagUser)) {
                Tag tag = findById(tagAssets.getTagId());
                BusinessException.throwException(tag.getTagCode() + MessageI18nUtil.getMessage("4000045"));
            }
            TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPatient)) {
                Tag tag = findById(tagPatient.getTagId());
                BusinessException.throwException(tag.getTagCode() + MessageI18nUtil.getMessage("4000046"));
            }
            TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(deleteDto.getId());
            if (Objects.nonNull(tagPostdocs)) {
                Tag tag = findById(tagPostdocs.getTagId());
                BusinessException.throwException(tag.getTagCode() + MessageI18nUtil.getMessage("4000047"));
            }
            currentPositionExposeService.delete(null,null,deleteDto.getId());
        });

        deleteDtoList.forEach(deleteDto -> {
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
        });
    }

    @Override
    public IPageResultData<List<ListTagVo>> list(Long departmentId, TagUseState useState, Integer battery, String tagCode, TagType type, TagPurpose purpose, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(tagCode)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_tagCode",tagCode);
        }
        if (Objects.nonNull(departmentId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
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
        if (Objects.nonNull(useState)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_useState",useState);
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
                Department department = departmentExposeService.findById(tag.getDepartmentId());
                if (Objects.nonNull(department)){
                    vo.setDepartmentName(department.getName());
                }
            }
            if (Objects.equals(tag.getPurpose(),TagPurpose.STAFF)) {
                TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagUser)) {
                    User user = userExposeService.findById(tagUser.getUserId());
                    if (Objects.nonNull(user)) {
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
                    Patient patient = patientExposeService.findById(tagPatient.getPatientId());
                    if (Objects.nonNull(patient)) {
                        vo.setBindingName(patient.getName());
                        vo.setBindingId(patient.getId());
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.POSTDOCS)) {
                TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagPostdocs)) {
                    TemporaryPerson temporaryPerson = temporaryPersonExposeService.findById(tagPostdocs.getPostdocsId());
                    if (Objects.nonNull(temporaryPerson)) {
                        vo.setBindingName(temporaryPerson.getName());
                        vo.setBindingId(temporaryPerson.getId());
                    }
                }
            }else if (Objects.equals(tag.getPurpose(),TagPurpose.ASSETS)) {
                TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
                if (Objects.nonNull(tagAssets)){
                    Assets assets = assetsExposeService.findById(tagAssets.getAssetsId());
                    if (Objects.nonNull(assets)){
                        vo.setBindingName(assets.getName());
                        vo.setBindingId(assets.getId());
                    }
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
        Tag tag = findById(id);
        if (Objects.isNull(tag)) {
            return null;
        }
        DetailsTagVo vo = new DetailsTagVo();
        BeanUtils.copyProperties(tag,vo);
        TagUser tagUser = tagUserExposeService.find(tag.getId());
        if (Objects.nonNull(tagUser)){
            User user = userExposeService.findById(tagUser.getUserId());
            if (Objects.nonNull(user)){
                vo.setBindingName(user.getName());
                vo.setImg(user.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                vo.setBindingId(user.getId());
            }
            return vo;
        }
        TagAssets tagAssets = tagAssetsExposeService.findByTagId(tag.getId());
        if (Objects.nonNull(tagAssets)) {
            Assets assets = assetsExposeService.findById(tagAssets.getAssetsId());
            if (Objects.nonNull(assets)) {
                vo.setBindingName(assets.getName());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setBindingId(assets.getId());
            }
            return vo;
        }
        TagPatient tagPatient = tagPatientExposeService.find(tag.getId());
        if (Objects.nonNull(tagPatient)) {
            Patient patient = patientExposeService.findById(tagPatient.getPatientId());
            if (Objects.nonNull(patient)) {
                vo.setBindingName(patient.getName());
                vo.setImg(patient.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(patient.getHeadPortrait()));
                vo.setBindingId(patient.getId());
            }
            return vo;
        }
        TagPostdocs tagPostdocs = tagPostdocsExposeService.find(tag.getId());
        if (Objects.nonNull(tagPostdocs)) {
            TemporaryPerson temporaryPerson = temporaryPersonExposeService.findById(tagPostdocs.getPostdocsId());
            if (Objects.nonNull(temporaryPerson)) {
                vo.setBindingName(temporaryPerson.getName());
                vo.setImg(temporaryPerson.getHeadPortrait());
                vo.setImgUrl(fileExposeService.getUrl(temporaryPerson.getHeadPortrait()));
                vo.setBindingId(temporaryPerson.getId());
            }
            return vo;
        }
        Department department = departmentExposeService.findById(tag.getDepartmentId());
        if (Objects.nonNull(department)) {
            vo.setDepartmentName(department.getName());
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
                BusinessException.throwException(MessageI18nUtil.getMessage("该设备名称已存在"));
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
        Department department = departmentExposeService.findById(departmentId);
        if (Objects.isNull(department) ){
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
