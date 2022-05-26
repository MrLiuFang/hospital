package com.lion.manage.service.rule.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.rule.LoseDao;
import com.lion.manage.entity.rule.Lose;
import com.lion.manage.service.rule.LoseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoseServiceImpl extends BaseServiceImpl<Lose> implements LoseService {

    @Autowired
    private LoseDao loseDao;
}
