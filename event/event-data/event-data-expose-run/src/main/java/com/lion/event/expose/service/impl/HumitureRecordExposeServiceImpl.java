package com.lion.event.expose.service.impl;

import com.lion.event.entity.HumitureRecord;
import com.lion.event.expose.service.HumitureRecordExposeService;
import com.lion.event.service.HumitureRecordService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/7/3 上午9:23
 */
@DubboService(interfaceClass = HumitureRecordExposeService.class)
public class HumitureRecordExposeServiceImpl implements HumitureRecordExposeService {

    @Autowired
    private HumitureRecordService humitureRecordService;

    @Override
    public HumitureRecord findLast(Long tagId) {
        return humitureRecordService.findLast(tagId);
    }
}
