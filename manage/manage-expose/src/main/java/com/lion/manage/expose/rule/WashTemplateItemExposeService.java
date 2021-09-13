package com.lion.manage.expose.rule;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashTemplateItem;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/13 下午4:32
 */
public interface WashTemplateItemExposeService extends BaseService<WashTemplateItem> {

    /**
     * 根据模板ID查询项
     * @param washTemplateId
     * @return
     */
    List<WashTemplateItem>  findByWashTemplateId(Long washTemplateId);
}
