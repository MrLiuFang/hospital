package com.lion.device.service.tag.impl;

import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagRuleDao;
import com.lion.device.dao.tag.TagRuleLogDao;
import com.lion.device.dao.tag.TagRuleUserDao;
import com.lion.device.entity.enums.TagRuleLogType;
import com.lion.device.entity.tag.*;
import com.lion.device.entity.tag.dto.AddTagRuleDto;
import com.lion.device.entity.tag.dto.UpdateTagRuleDto;
import com.lion.device.service.tag.TagRuleLogService;
import com.lion.device.service.tag.TagRuleService;
import com.lion.device.service.tag.TagRuleUserService;
import com.lion.exception.BusinessException;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/4 上午11:05
 **/
@Service
public class TagRuleServiceImpl extends BaseServiceImpl<TagRule> implements TagRuleService {

    @Autowired
    private TagRuleDao tagRuleDao;

    @Autowired
    private TagRuleLogService tagRuleLogService;

    @Autowired
    private TagRuleLogDao tagRuleLogDao;

    @Autowired
    private TagRuleUserDao tagRuleUserDao;

    @Autowired
    private TagRuleUserService tagRuleUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void add(AddTagRuleDto addTagRuleDto) {
        TagRule tagRule = new TagRule();
        assertNameExist(tagRule.getName(),null);
        BeanUtils.copyProperties(addTagRuleDto,tagRule);
        tagRule = save(tagRule);
        tagRuleUserService.relationUser(addTagRuleDto.getUserIds(), Collections.EMPTY_LIST,Collections.EMPTY_LIST , tagRule.getId());
        tagRuleLogService.add(tagRule.getId(),"新建规则("+tagRule.getName()+")", TagRuleLogType.ADD);
        redisTemplate.opsForValue().set(RedisConstants.TAG_RULE+tagRule.getId(),tagRule,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    @Transactional
    public void update(UpdateTagRuleDto updateTagRuleDto) {
        TagRule tagRule = new TagRule();
        TagRule oldTagRule = this.findById(updateTagRuleDto.getId());
        BeanUtils.copyProperties(updateTagRuleDto,tagRule);
        assertNameExist(tagRule.getName(),tagRule.getId());
        update(tagRule);
        tagRuleUserService.relationUser(updateTagRuleDto.getNewUserIds(), updateTagRuleDto.getDeleteUserIds(),updateTagRuleDto.getAllUserIds() , tagRule.getId());
        tagRuleLogService.add(tagRule.getId(),"修改规则("+(Objects.nonNull(oldTagRule)?oldTagRule.getName():"")+")", TagRuleLogType.UPDATE);
        redisTemplate.opsForValue().set(RedisConstants.TAG_RULE+tagRule.getId(),tagRule,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
            tagRuleLogDao.deleteByTagRuleId(deleteDto.getId());
            tagRuleUserDao.deleteByTagRuleId(deleteDto.getId());
            redisTemplate.delete(RedisConstants.TAG_RULE+deleteDto.getId());
            TagRule tagRule = this.findById(deleteDto.getId());
            tagRuleLogService.add(tagRule.getId(),"删除规则("+(Objects.nonNull(tagRule)?tagRule.getName():"")+")", TagRuleLogType.DELETE);
        });
    }

    private void assertNameExist(String name, Long id) {
        TagRule tagRule = tagRuleDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(tagRule) ) || (Objects.nonNull(id) && Objects.nonNull(tagRule) && !Objects.equals(tagRule.getId(),id))){
            BusinessException.throwException(MessageI18nUtil.getMessage("4000042"));
        }
    }
}
