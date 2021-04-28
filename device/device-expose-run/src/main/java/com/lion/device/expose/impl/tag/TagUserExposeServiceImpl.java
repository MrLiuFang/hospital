package com.lion.device.expose.impl.tag;

import com.lion.common.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagUserDao;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagUser;
import com.lion.device.expose.tag.TagUserExposeService;
import com.lion.exception.BusinessException;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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
    private TagDao tagDao;

    @Autowired
    private TagUserDao tagUserDao;

    @Override
    public void binding(Long userId, String tagCode) {
        if (!StringUtils.hasText(tagCode)){
            unbinding(userId,false);
        }
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            return;
        }
        TagUser tagUser = tagUserDao.findFirstByUserIdAndUnbindingTimeIsNull(userId);
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
        redisTemplate.opsForValue().set(RedisConstants.TAG_USER+tag.getId(),userId, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(RedisConstants.USER_TAG+userId,tag.getId(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void unbinding(Long userId,Boolean isDelete) {
        TagUser tagUser = tagUserDao.findFirstByUserIdAndUnbindingTimeIsNull(userId);
        if (Objects.equals(true,isDelete)) {
            tagUserDao.deleteByUserId(userId);
        }else {
            if (Objects.nonNull(tagUser)) {
                tagUser.setUnbindingTime(LocalDateTime.now());
                update(tagUser);
            }
        }
        if (Objects.nonNull(tagUser)) {
            redisTemplate.delete(RedisConstants.TAG_USER + tagUser.getTagId());
            redisTemplate.delete(RedisConstants.USER_TAG + tagUser.getUserId());
        }
    }

    @Override
    public TagUser find(Long tagId) {
        List<TagUser> list = tagUserDao.findByTagIdAndUnbindingTimeIsNull(tagId);
        return list.size()>0?list.get(0):null;
    }


}
