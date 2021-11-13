package com.lion.manage.service.template.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.template.SearchTemplateDao;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.template.SearchTemplate;
import com.lion.manage.entity.template.dto.AddSearchTemplateDto;
import com.lion.manage.entity.template.dto.UpdateSearchTemplateDto;
import com.lion.manage.entity.ward.Ward;
import com.lion.manage.entity.ward.WardRoom;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.service.template.SearchTemplateService;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SearchTemplateServiceImpl extends BaseServiceImpl<SearchTemplate> implements SearchTemplateService {

    @Autowired
    private SearchTemplateDao searchTemplateDao;

    @Override
    public void add(AddSearchTemplateDto addSearchTemplateDto) {
        SearchTemplate searchTemplate = new SearchTemplate();
        BeanUtils.copyProperties(addSearchTemplateDto,searchTemplate);
        assertNameExist(searchTemplate.getName(),null);
        save(searchTemplate);
    }

    @Override
    public void update(UpdateSearchTemplateDto updateSearchTemplateDto) {
        SearchTemplate searchTemplate = new SearchTemplate();
        BeanUtils.copyProperties(updateSearchTemplateDto,searchTemplate);
        assertNameExist(searchTemplate.getName(),searchTemplate.getId());
        update(searchTemplate);
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
        });
    }

    private void assertNameExist(String name, Long id) {
        SearchTemplate searchTemplate = searchTemplateDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(searchTemplate)) || (Objects.nonNull(id) && Objects.nonNull(searchTemplate) && !Objects.equals(searchTemplate.getId(),id) ) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000121"));
        }
    }
}
