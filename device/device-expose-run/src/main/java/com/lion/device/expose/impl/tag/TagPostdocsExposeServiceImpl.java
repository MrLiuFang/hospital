package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.Type;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagPostdocsDao;
import com.lion.device.entity.enums.*;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagPatient;
import com.lion.device.entity.tag.TagPostdocs;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:38
 */
@DubboService(interfaceClass = TagPostdocsExposeService.class)
public class TagPostdocsExposeServiceImpl extends BaseServiceImpl<TagPostdocs> implements TagPostdocsExposeService {

    @Autowired
    private TagPostdocsDao tagPostdocsDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagLogService tagLogService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void binding(Long postdocsId, String tagCode) {
        if (!StringUtils.hasText(tagCode)){
            unbinding(postdocsId,false);
            return;
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000021"));
        }
        if (Objects.equals(tag.getDeviceState(), State.NOT_ACTIVE)) {
            BusinessException.throwException(tag.getTagCode() +"未激活不能使用");
        }
//        if (Objects.equals(tag.getState(), TagState.DISABLE)) {
//            BusinessException.throwException(MessageI18nUtil.getMessage("4000025"));
//        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.POSTDOCS)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000029"));
        }
        TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(tagPostdocs)){
            if (!Objects.equals( tagPostdocs.getPostdocsId(),postdocsId)){
                BusinessException.throwException(MessageI18nUtil.getMessage("4000030"));
            }else {
                return;
            }
        }
        TagPostdocs newTagPostdocs = new TagPostdocs();
        newTagPostdocs.setTagId(tag.getId());
        newTagPostdocs.setPostdocsId(postdocsId);
        save(newTagPostdocs);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setDeviceState(State.USED);
        tagService.update(tag);
        redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
        redisTemplate.opsForValue().set(RedisConstants.TAG_TEMPORARY_PERSON+tag.getId(),postdocsId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON_TAG+postdocsId,tag.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tag.getId(), Type.MIGRANT, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void unbinding(Long postdocsId, Boolean isDelete) {
        TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByPostdocsIdAndUnbindingTimeIsNull(postdocsId);
        if (Objects.equals(true,isDelete)) {
            tagPostdocsDao.deleteByPostdocsId(postdocsId);
        }
        if (Objects.nonNull(tagPostdocs)) {
            tagPostdocs.setUnbindingTime(LocalDateTime.now());
            update(tagPostdocs);
            tagLogService.add( TagLogContent.unbinding,tagPostdocs.getTagId());
            com.lion.core.Optional<Tag> optional = tagService.findById(tagPostdocs.getTagId());
            if (optional.isPresent()) {
                Tag tag = optional.get();
                tag.setDeviceState(State.NOT_USED);
                tagService.update(tag);
            }
            redisTemplate.delete(RedisConstants.TAG_TEMPORARY_PERSON + tagPostdocs.getTagId());
            redisTemplate.delete(RedisConstants.TEMPORARY_PERSON_TAG + tagPostdocs.getPostdocsId());
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tagPostdocs.getTagId());
        }
    }

    @Override
    public TagPostdocs find(Long tagId) {
        return tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
    }
}
