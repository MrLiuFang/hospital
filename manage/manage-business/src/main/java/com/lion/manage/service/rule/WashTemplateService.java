package com.lion.manage.service.rule;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.dto.AddWashTemplateDto;
import com.lion.manage.entity.rule.dto.UpdateWashTemplateDto;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateVo;
import com.lion.upms.entity.user.dto.AddUserTypeDto;
import com.lion.upms.entity.user.dto.UpdateUserTypeDto;
import com.lion.upms.entity.user.vo.ListUserTypeVo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:53
 */
public interface WashTemplateService extends BaseService<WashTemplate> {

    /**
     * 新增
     * @param addWashTemplateDto
     */
    public void add(AddWashTemplateDto addWashTemplateDto);

    /**
     * 修改
     * @param updateWashTemplateDto
     */
    public void update(UpdateWashTemplateDto updateWashTemplateDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

    /**
     * 列表
     * @param name
     * @param LionPage
     * @return
     */
    public IPageResultData<List<ListWashTemplateVo>> list(String name, LionPage LionPage);

    /**
     * 详情
     * @param id
     * @return
     */
    DetailsWashTemplateVo details( Long id);
}
