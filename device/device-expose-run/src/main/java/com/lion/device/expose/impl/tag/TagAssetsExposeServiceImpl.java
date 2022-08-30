package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.common.enums.Type;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagAssetsDao;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagLogContent;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagUseState;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.service.tag.TagAssetsService;
import com.lion.device.service.tag.TagLogService;
import com.lion.device.service.tag.TagService;
import com.lion.exception.BusinessException;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:25
 */
@DubboService(interfaceClass = TagAssetsExposeService.class)
public class TagAssetsExposeServiceImpl extends BaseServiceImpl<TagAssets> implements TagAssetsExposeService {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagAssetsService tagAssetsService;

    @Autowired
    private TagAssetsDao tagAssetsDao;

    @Autowired
    private TagLogService tagLogService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Boolean relation(Long assetsId, String tagCode, Long departmentId) {
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.isNull(tag)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000021"));
        }
//        if (Objects.equals(tag.getDeviceState(), State.NOT_ACTIVE)) {
//            BusinessException.throwException(tag.getTagCode() +"未激活不能使用");
//        }
        if (!Objects.equals(tag.getPurpose(), TagPurpose.ASSETS)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000022"));
        }
        if (!Objects.equals(tag.getDepartmentId(), departmentId)){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000023"));
        }
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        if (Objects.nonNull(tagAssets)){
            if (!Objects.equals( tagAssets.getAssetsId(), assetsId)){
                BusinessException.throwException(MessageI18nUtil.getMessage("4000024"));
            }else {
                return true;
            }
        }
//        else {
//            if (Objects.equals(tag.getUseState(), TagUseState.USEING)){
//                BusinessException.throwException("该标签正在使用中");
//            }
//        }
        TagAssets newTagAssets = new TagAssets();
        newTagAssets.setAssetsId(assetsId);
        newTagAssets.setTagId(tag.getId());
        newTagAssets.setBindingTime(LocalDateTime.now());
        tagAssetsService.save(newTagAssets);
        tagLogService.add( TagLogContent.binding,tag.getId());
        tag.setDeviceState(State.USED);
        tagService.update(tag);
        redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
        redisTemplate.opsForValue().set(RedisConstants.TAG_BIND_TYPE+tag.getId(), Type.ASSET, 5, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean unrelation(Long assetsId) {
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        if (Objects.nonNull(tagAssets)){
            tagAssets.setUnbindingTime(LocalDateTime.now());
            tagAssetsService.update(tagAssets);
            com.lion.core.Optional<Tag> optional = tagService.findById(tagAssets.getTagId());
            if (optional.isPresent()){
                Tag tag = optional.get();
                tag.setDeviceState(State.NOT_USED);
                tagService.update(tag);
                tagLogService.add( TagLogContent.unbinding,tagAssets.getTagId());
                redisTemplate.delete(RedisConstants.TAG_BIND_TYPE + tag.getId());
            }
            redisTemplate.delete(RedisConstants.TAG_BIND_TYPE+tagAssets.getTagId());
        }
        return true;
    }

    @Override
    public Boolean deleteByAssetsId(Long assetsId) {
        unrelation(assetsId);
        tagAssetsDao.deleteByAssetsId(assetsId);
        return true;
    }

    @Override
    public TagAssets find(Long assetsId) {
        TagAssets tagAssets = tagAssetsDao.findFirstByAssetsIdAndUnbindingTimeIsNull(assetsId);
        return tagAssets;
    }

    @Override
    public TagAssets findByTagId(Long tagId) {
        return tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tagId);
    }

    @Override
    public TagAssets findByTagCode(String tagCode) {
        Tag tag = tagDao.findFirstByTagCode(tagCode);
        if (Objects.nonNull(tag)) {
            TagAssets tagAssets = tagAssetsDao.findFirstByTagIdAndUnbindingTimeIsNull(tag.getId());
            if (Objects.nonNull(tagAssets)) {
                return tagAssets;
            }
        }
        return null;
    }
}
