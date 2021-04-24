package com.lion.device.expose.impl.tag;

import com.lion.common.ResdisConstants;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.device.service.tag.TagService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
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
        Tag tag = tagService.findById(tagId);
        if (Objects.nonNull(tag)) {
            tag.setBattery(battery);
            update(tag);
            redisTemplate.opsForValue().set(ResdisConstants.TAG_CODE+tag.getTagCode(),tag,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(ResdisConstants.TAG+tag.getId(),tag,ResdisConstants.EXPIRE_TIME, TimeUnit.DAYS);
        }
    }
}
