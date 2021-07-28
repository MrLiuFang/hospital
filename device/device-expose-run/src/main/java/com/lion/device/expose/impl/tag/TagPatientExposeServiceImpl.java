package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagPatientDao;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagPatient;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
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


    @Override
    @Transactional
    public void binding(Long patientId, String tagCode, Long departmentId) {
        if (!StringUtils.hasText(tagCode)){
            unbinding(patientId,false);
            return;
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException("该标签不存在");
        }
        if (Objects.equals(tag.getState(), TagState.DISABLE)) {
            BusinessException.throwException("该表标签处于停用状态，可能在回收箱中,未消毒");
        }
        if (!Objects.equals(departmentId,tag.getDepartmentId())) {
            BusinessException.throwException("该表标签患者不在同一科室不能绑定");
        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.PATIENT)){
            BusinessException.throwException("该标签不能与患者绑定");
        }
        TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(tagPatient)){
            if (!Objects.equals( tagPatient.getPatientId(),patientId)){
                BusinessException.throwException("该标签已被其它患者绑定");
            }else {
                return;
            }
        }
        TagPatient newTagPatient = new TagPatient();
        newTagPatient.setTagId(tag.getId());
        newTagPatient.setPatientId(patientId);
        save(newTagPatient);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setUseState(TagUseState.USEING);
        tagService.update(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG_PATIENT+tag.getId(),patientId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.PATIENT_TAG+patientId,tag.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void unbinding(Long patientId, Boolean isDelete) {
        TagPatient tagPatient = tagPatientDao.findFirstByPatientIdAndUnbindingTimeIsNull(patientId);
        if (Objects.equals(true,isDelete)) {
            tagPatientDao.deleteByPatientId(patientId);
        }else {
            if (Objects.nonNull(tagPatient)) {
                tagPatient.setUnbindingTime(LocalDateTime.now());
                update(tagPatient);
                tagLogService.add( TagLogContent.unbinding,tagPatient.getTagId());
            }
        }
        if (Objects.nonNull(tagPatient)) {
            Tag tag = tagService.findById(tagPatient.getTagId());
            tag.setUseState(TagUseState.NOT_USED);
            tagService.update(tag);
            redisTemplate.delete(RedisConstants.TAG_PATIENT + tagPatient.getTagId());
            redisTemplate.delete(RedisConstants.PATIENT_TAG + tagPatient.getPatientId());
        }

    }

    @Override
    public TagPatient find(Long tagId) {
        TagPatient tagPatient = tagPatientDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
        return tagPatient;
    }
}
