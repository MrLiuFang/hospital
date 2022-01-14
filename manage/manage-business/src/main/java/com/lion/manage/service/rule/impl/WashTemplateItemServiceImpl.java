package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.WashTemplateItemDao;
import com.lion.manage.entity.rule.WashTemplateItem;
import com.lion.manage.entity.rule.dto.AddWashTemplateItemDto;
import com.lion.manage.service.rule.WashDeviceTypeService;
import com.lion.manage.service.rule.WashTemplateItemService;
import com.lion.manage.service.rule.WashTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:55
 */
@Service
public class WashTemplateItemServiceImpl extends BaseServiceImpl<WashTemplateItem> implements WashTemplateItemService {

    @Autowired
    private WashTemplateItemDao washTemplateItemDao;

    @Autowired
    private WashDeviceTypeService washDeviceTypeService;

    @Override
    @Transactional
    public void add(List<AddWashTemplateItemDto> list, Long washTemplateId) {
        washTemplateItemDao.deleteByWashTemplateId(washTemplateId);
        list.forEach(dto->{
            WashTemplateItem washTemplateItem =new WashTemplateItem();
            BeanUtils.copyProperties(dto, washTemplateItem);
            washTemplateItem.setWashTemplateId(washTemplateId);
            washTemplateItem.setId(null);
            washTemplateItem = save(washTemplateItem);
            washDeviceTypeService.add(washTemplateItem.getId(),dto.getDeviceTypes());
        });
    }
}
