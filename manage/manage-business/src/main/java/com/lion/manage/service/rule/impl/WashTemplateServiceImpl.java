package com.lion.manage.service.rule.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashTemplateDao;
import com.lion.manage.entity.rule.WashTemplate;
import com.lion.manage.entity.rule.dto.AddWashTemplateDto;
import com.lion.manage.entity.rule.dto.UpdateWashTemplateDto;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateVo;
import com.lion.manage.service.rule.WashTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:53
 */
@Service
public class WashTemplateServiceImpl extends BaseServiceImpl<WashTemplate> implements WashTemplateService {

    @Autowired
    private WashTemplateDao washTemplateDao;

    @Autowired
    private WashTemplateService washTemplateService;

    @Override
    public void add(AddWashTemplateDto addWashTemplateDto) {

    }

    @Override
    public void update(UpdateWashTemplateDto updateWashTemplateDto) {

    }

    @Override
    public void delete(List<DeleteDto> deleteDto) {

    }

    @Override
    public IPageResultData<List<ListWashTemplateVo>> list(String name, LionPage LionPage) {
        return null;
    }

    @Override
    public DetailsWashTemplateVo details(Long id) {
        return null;
    }
}
