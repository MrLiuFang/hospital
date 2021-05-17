package com.lion.manage.expose.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.service.assets.AssetsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/16 上午9:50
 **/
@DubboService
public class AssetsExposeServiceImpl extends BaseServiceImpl<Assets> implements AssetsExposeService {

    @Autowired
    private AssetsService assetsService;

    @Override
    public Assets find(Long tagId) {
        return assetsService.findByTagId(tagId);
    }
}
