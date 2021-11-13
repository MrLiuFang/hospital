package com.lion.manage.service.template;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.template.SearchTemplate;
import com.lion.manage.entity.template.dto.AddSearchTemplateDto;
import com.lion.manage.entity.template.dto.UpdateSearchTemplateDto;
import com.lion.manage.entity.ward.dto.AddWardDto;
import com.lion.manage.entity.ward.dto.UpdateWardDto;

import java.util.List;

public interface SearchTemplateService extends BaseService<SearchTemplate> {

    /**
     * 新增搜索模板
     * @param addSearchTemplateDto
     */
    public void add(AddSearchTemplateDto addSearchTemplateDto);

    /**
     * 修改新增搜索模板
     * @param updateSearchTemplateDto
     */
    public void update(UpdateSearchTemplateDto updateSearchTemplateDto);

    /**
     * 删除新增搜索模板
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
