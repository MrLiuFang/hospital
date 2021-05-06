package com.lion.event.service.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.dao.WashDao;
import com.lion.event.entity.Wash;
import com.lion.event.service.WashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:46
 **/
@Service
public class WashServiceImpl implements WashService {

    @Autowired
    private WashDao washDao;

    @Override
    public void save(Wash wash) {
        washDao.save(wash);
    }

    @Override
    public IPageResultData<List<Wash>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            endDateTime = LocalDateTime.now();
            startDateTime = endDateTime.minusDays(7);
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            endDateTime = startDateTime.plusMinutes(7);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            startDateTime = endDateTime.minusDays(7);
        }
        return washDao.list(userId, startDateTime, endDateTime, lionPage);
    }
}
