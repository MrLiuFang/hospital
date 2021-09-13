package com.lion.manage.expose.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashTemplateItemDao;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.expose.rule.WashTemplateItemExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/13 下午4:33
 */
@DubboService
public class WashTemplateItemExposeServiceImpl extends BaseServiceImpl<WashTemplateItem> implements WashTemplateItemExposeService {

    @Autowired
    private WashTemplateItemDao washTemplateItemDao;

    @Override
    public List<WashTemplateItem> findByWashTemplateId(Long washTemplateId) {
        return washTemplateItemDao.findByWashTemplateId(washTemplateId);
    }
}
