package com.lion.manage.service.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.dto.AddWashTemplateDto;
import com.lion.manage.entity.rule.dto.AddWashTemplateItemDto;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:54
 */
public interface WashTemplateItemService extends BaseService<WashTemplateItem> {

    /**
     * 新增规则项
     * @param list
     * @param washTemplateId
     */
    public void add(List<AddWashTemplateItemDto> list, Long washTemplateId);
}
