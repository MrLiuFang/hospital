package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagUserDao;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/24 上午9:40
 **/
@DubboService(interfaceClass = TagUserExposeService.class)
public class TagUserExposeServiceImpl extends BaseServiceImpl<TagUser> implements TagUserExposeService {

    @Autowired
    private RedisTemplate redisTemplate;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private TagLogService tagLogService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagUserDao tagUserDao;

    @Override
    @Transactional
    public void binding(Long userId, String tagCode, Long departmentId) {
        if (!StringUtils.hasText(tagCode)){
            unbinding(userId,false);
            return;
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        TagUser tagUser = tagUserDao.findFirstByUserIdAndUnbindingTimeIsNull(userId);
        if (Objects.isNull(tag)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000021"));
        }
        if (Objects.nonNull(tagUser) && !Objects.equals(tag.getId(),tagUser.getTagId())) {
            unbinding(userId,false);
        }
        if (Objects.equals(tag.getState(), TagState.DISABLE)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("4000025"));
        }
        if (!Objects.equals(tag.getPurpose(),TagPurpose.STAFF)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("4000031"));
        }
        if (!Objects.equals(departmentId,tag.getDepartmentId())) {
            BusinessException.throwException(MessageI18nUtil.getMessage("4000032"));
        }
        TagUser oldTagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(oldTagUser)){
            if (!Objects.equals( oldTagUser.getUserId(),userId)){
                BusinessException.throwException(MessageI18nUtil.getMessage("4000033"));
            }else {
                return;
            }
        }
        TagUser newTagUser = new TagUser();
        newTagUser.setTagId(tag.getId());
        newTagUser.setUserId(userId);
        save(newTagUser);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setUseState(TagUseState.USEING);
        tagService.update(tag);
        redisTemplate.opsForValue().set(RedisConstants.TAG_USER+tag.getId(),userId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.USER_TAG+userId,tag.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void unbinding(Long userId,Boolean isDelete) {
        TagUser tagUser = tagUserDao.findFirstByUserIdAndUnbindingTimeIsNull(userId);
        if (Objects.equals(true,isDelete)) {
            tagUserDao.deleteByUserId(userId);
        }else {
            if (Objects.nonNull(tagUser)) {
                tagUser.setUnbindingTime(LocalDateTime.now());
                update(tagUser);
                tagLogService.add( TagLogContent.unbinding,tagUser.getTagId());
            }
        }
        if (Objects.nonNull(tagUser)) {
            Tag tag = tagService.findById(tagUser.getTagId());
            tag.setUseState(TagUseState.NOT_USED);
            tagService.update(tag);
            redisTemplate.delete(RedisConstants.TAG_USER + tagUser.getTagId());
            redisTemplate.delete(RedisConstants.USER_TAG + tagUser.getUserId());
        }
    }

    @Override
    public TagUser find(Long tagId) {
        TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
        return tagUser;
    }

    @Override
    public TagUser findByUserId(Long userId) {
        return tagUserDao.findFirstByUserIdAndUnbindingTimeIsNull(userId);
    }


}
