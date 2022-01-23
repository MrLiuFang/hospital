package com.lion.device.expose.impl.tag;

import com.lion.common.constants.RedisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagState;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.service.tag.TagService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/7下午9:05
 */
@DubboService(interfaceClass = TagExposeService.class)
public class TagExposeServiceImpl extends BaseServiceImpl<Tag> implements TagExposeService {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Tag find(Long assetsId) {
        return tagDao.findByAssetsId(assetsId);
    }

    @Override
    public Tag find(String tagCode) {
        return tagDao.findFirstByTagCode(tagCode);
    }

    @Override
    public void updateBattery(Long tagId, Integer battery) {
        com.lion.core.Optional<Tag> optional = tagService.findById(tagId);
        if (optional.isPresent()) {
            Tag tag = optional.get();
            tag.setBattery(battery);
            update(tag);
            redisTemplate.opsForValue().set(RedisConstants.TAG_CODE+tag.getTagCode(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisConstants.TAG+tag.getId(),tag, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
    }

    @Override
    public Integer countTag(Long departmentId, Integer battery) {
        return tagDao.countByDepartmentIdAndBattery(departmentId,battery);
    }

    @Override
    public List<Tag> find(Long departmentId,TagPurpose purpose, String tagCode) {
        if (StringUtils.hasText(tagCode)) {
            return tagDao.findByDepartmentIdAndPurposeAndTagCodeLike(departmentId, purpose, "%"+tagCode+"%");
        }
        return tagDao.findByDepartmentIdAndPurpose(departmentId, purpose);
    }

    @Override
    public Integer countTag(Long departmentId) {
        return tagDao.countByDepartmentIdAndPurpose(departmentId,TagPurpose.THERMOHYGROGRAPH);
    }

    @Override
    public Integer countTag(Long departmentId, TagPurpose purpose, State deviceState) {
        if (Objects.isNull(deviceState)) {
            return tagDao.countByDepartmentIdAndPurpose(departmentId, purpose);
        }
        return tagDao.countByDepartmentIdAndPurposeAndDeviceState(departmentId, purpose,deviceState);
    }

    @Override
    public void updateDeviceState(Long id, Integer state) {
        tagDao.updateDeviceState(id, State.instance(state));
    }

    @Override
    public void updateState(Long id, Integer state) {
        tagDao.updateState(id, TagState.instance(state));
    }

    @Override
    public void updateDeviceDataTime(Long id, LocalDateTime dateTime) {
        tagDao.updateLastDataTime(id,dateTime);
    }

    @Override
    public List<Long> allId() {
        return tagDao.allId();
    }

    @Override
    public List<Long> allId(TagPurpose tagPurpose) {
        return tagDao.allIdByTagPurpose(tagPurpose);
    }

    @Override
    public List<Long> find(TagType tagType) {
        return tagDao.findId(tagType);
    }

    @Override
    public List<Long> find(TagType tagType, String tagCode) {
        List<Long> list = new ArrayList<Long>();
        if (Objects.nonNull(tagType) && Objects.nonNull(tagCode)) {
            list = tagDao.findId(tagType,tagCode);
        }else if (Objects.nonNull(tagType)){
            list = tagDao.findId(tagType);
        }else if (Objects.nonNull(tagCode)){
            list = tagDao.findId(tagCode);
        }
        if (Objects.nonNull(tagType) || Objects.nonNull(tagCode)) {
            if (list.size()<=0){
                list.add(Long.MAX_VALUE);
            }
        }
        return list;
    }
}
