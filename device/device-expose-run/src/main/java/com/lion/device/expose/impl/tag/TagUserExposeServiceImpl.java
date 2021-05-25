package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagUserDao;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.upms.expose.user.UserExposeService;
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
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException("该标签不存在");
        }
        if (!Objects.equals(tag.getPurpose(),TagPurpose.STAFF)) {
            BusinessException.throwException("此标签不能绑定员工");
        }
        if (!Objects.equals(departmentId,tag.getDepartmentId())) {
            BusinessException.throwException("该表标签与员工不在同一科室不能绑定");
        }
        TagUser tagUser = tagUserDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
        if (Objects.nonNull(tagUser)){
            if (!Objects.equals( tagUser.getUserId(),userId)){
                BusinessException.throwException("该标签已被其它用户绑定");
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
