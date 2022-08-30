package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.Type;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagPatientDao;
import com.lion.device.entity.enums.*;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagPatient;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:34
 */
@DubboService(interfaceClass = TagPatientExposeService.class)
public class TagPatientExposeServiceImpl extends BaseServiceImpl<TagPatient> implements TagPatientExposeService {

    @Autowired
    private TagPatientDao tagPatientDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagLogService tagLogService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TagService tagService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;


    @Override
    @Transactional
    public void binding(Long patientId, String tagCode, Long departmentId) {
        if (!StringUtils.hasText(tagCode)){
            unbinding(patientId,false);
            return;
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000021"));
        }
//        if (Objects.equals(tag.getDeviceState(), State.NOT_ACTIVE)) {
//            BusinessException.throwException(tag.getTagCode() +"未激活不能使用");
//        }
//        if (Objects.equals(tag.getState(), TagState.DISABLE)) {
//            BusinessException.throwException(MessageI18nUtil.getMessage("4000025"));
//        }
        if (!Objects.equals(departmentId,tag.getDepartmentId())) {
            com.lion.core.Optional<Department> optionalTagDepartment = departmentExposeService.findById(tag.getDepartmentId());
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(departmentId);
            if (optionalTagDepartment.isPresent() && optionalDepartment.isPresent()) {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000026", new Object[]{optionalTagDepartment.get().getName(), optionalDepartment.get().getName()}));
            }
        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.PATIENT)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000027"));
        }
        TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(tagPatient)){
            if (!Objects.equals( tagPatient.getPatientId(),patientId)){
                BusinessException.throwException(MessageI18nUtil.getMessage("4000028"));
            }else {
                return;
            }
        }
        TagPatient newTagPatient = new TagPatient();
        newTagPatient.setTagId(tag.getId());
        newTagPatient.setPatientId(patientId);
        save(newTagPatient);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setDeviceState(State.USED);
        tagService.update(tag);
        redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
        redisTemplate.opsForValue().set(RedisConstants.TAG_PATIENT+tag.getId(),patientId, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.PATIENT_TAG+patientId,tag.getId(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tag.getId(), Type.PATIENT, 5, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public void unbinding(Long patientId, Boolean isDelete) {
        TagPatient tagPatient = tagPatientDao.findFirstByPatientIdAndUnbindingTimeIsNull(patientId);
        if (Objects.equals(true,isDelete)) {
            tagPatientDao.deleteByPatientId(patientId);
            Long tagId = (Long) redisTemplate.opsForValue().get(RedisConstants.PATIENT_TAG+patientId);
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagId);
        }
        if (Objects.nonNull(tagPatient)) {
            tagPatient.setUnbindingTime(LocalDateTime.now());
            update(tagPatient);
            tagLogService.add( TagLogContent.unbinding,tagPatient.getTagId());
            com.lion.core.Optional<Tag> optional = tagService.findById(tagPatient.getTagId());
            if (optional.isPresent()) {
                Tag tag = optional.get();
                tag.setDeviceState(State.NOT_USED);
                tagService.update(tag);
                redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
            }
            redisTemplate.delete(RedisConstants.TAG_PATIENT + tagPatient.getTagId());
            redisTemplate.delete(RedisConstants.PATIENT_TAG + tagPatient.getPatientId());
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagPatient.getTagId());
        }

    }

    @Override
    public TagPatient find(Long tagId) {
        TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
        return tagPatient;
    }
}
