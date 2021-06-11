package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagPostdocsDao;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagPatient;
import com.lion.device.entity.tag.TagPostdocs;
import com.lion.device.expose.tag.TagPostdocsExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
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
            BusinessException.throwException("该标签不存在");
        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.POSTDOCS)){
            BusinessException.throwException("该标签不能与流动人员绑定");
        }
        TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(tagPostdocs)){
            if (!Objects.equals( tagPostdocs.getPostdocsId(),postdocsId)){
                BusinessException.throwException("该标签已被其它流动人员绑定");
            }else {
                return;
            }
        }
        TagPostdocs newTagPostdocs = new TagPostdocs();
        newTagPostdocs.setTagId(tag.getId());
        newTagPostdocs.setPostdocsId(postdocsId);
        save(newTagPostdocs);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setUseState(TagUseState.USEING);
        tagService.update(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG_TEMPORARY_PERSON+tag.getId(),postdocsId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.TEMPORARY_PERSON_TAG+postdocsId,tag.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void unbinding(Long postdocsId, Boolean isDelete) {
        TagPostdocs tagPostdocs = tagPostdocsDao.findFirstByPostdocsIdAndUnbindingTimeIsNull(postdocsId);
        if (Objects.equals(true,isDelete)) {
            tagPostdocsDao.deleteByPostdocsId(postdocsId);
        }else {
            if (Objects.nonNull(tagPostdocs)) {
                tagPostdocs.setUnbindingTime(LocalDateTime.now());
                update(tagPostdocs);
                tagLogService.add( TagLogContent.unbinding,tagPostdocs.getTagId());
            }
        }
        if (Objects.nonNull(tagPostdocs)) {
            Tag tag = tagService.findById(tagPostdocs.getTagId());
            tag.setUseState(TagUseState.NOT_USED);
            tagService.update(tag);
        }
        redisTemplate.delete(RedisConstants.TAG_TEMPORARY_PERSON + tagPostdocs.getTagId());
        redisTemplate.delete(RedisConstants.TEMPORARY_PERSON_TAG + tagPostdocs.getPostdocsId());
    }

    @Override
    public TagPostdocs find(Long tagId) {
        return tagPostdocsDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
    }
}
