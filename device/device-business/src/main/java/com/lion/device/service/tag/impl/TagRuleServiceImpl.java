package com.lion.device.service.tag.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.tag.TagDao;
import com.lion.device.dao.tag.TagRuleDao;
import com.lion.device.dao.tag.TagRuleLogDao;
import com.lion.device.dao.tag.TagRuleUserDao;
import com.lion.device.entity.tag.*;
import com.lion.device.entity.tag.dto.AddTagRuleDto;
import com.lion.device.entity.tag.dto.UpdateTagRuleDto;
import com.lion.device.service.tag.TagRuleService;
import com.lion.device.service.tag.TagRuleUserService;
import com.lion.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/4 上午11:05
 **/
@Service
public class TagRuleServiceImpl extends BaseServiceImpl<TagRule> implements TagRuleService {

    @Autowired
    private TagRuleDao tagRuleDao;

    @Autowired
    private TagRuleLogDao tagRuleLogDao;

    @Autowired
    private TagRuleUserDao tagRuleUserDao;

    @Autowired
    private TagRuleUserService tagRuleUserService;

    @Override
    @Transactional
    public void add(AddTagRuleDto addTagRuleDto) {
        TagRule tagRule = new TagRule();
        assertNameExist(tagRule.getName(),null);
        BeanUtils.copyProperties(addTagRuleDto,tagRule);
        tagRule = save(tagRule);
        tagRuleUserService.relationUser(addTagRuleDto.getUserIds(), Collections.EMPTY_LIST,tagRule.getId());
    }

    @Override
    @Transactional
    public void update(UpdateTagRuleDto updateTagRuleDto) {
        TagRule tagRule = new TagRule();
        assertNameExist(tagRule.getName(),null);
        BeanUtils.copyProperties(updateTagRuleDto,tagRule);
        update(tagRule);
        tagRuleUserService.relationUser(updateTagRuleDto.getNewUserIds(), updateTagRuleDto.getDeleteUserIds(),tagRule.getId());
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
            tagRuleLogDao.deleteByTagRuleId(deleteDto.getId());
            tagRuleUserDao.deleteByTagRuleId(deleteDto.getId());
        });
    }

    private void assertNameExist(String name, Long id) {
        TagRule tagRule = tagRuleDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(tagRule) ) || (Objects.nonNull(id) && Objects.nonNull(tagRule) && !Objects.equals(tagRule.getId(),id))){
            BusinessException.throwException("该规则名称已存在");
        }
    }
}
