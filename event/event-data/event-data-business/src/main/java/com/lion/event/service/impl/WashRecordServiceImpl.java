package com.lion.event.service.impl;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.dao.WashEventDao;
import com.lion.event.dao.WashRecordDao;
import com.lion.event.entity.WashRecord;
import com.lion.event.service.WashRecordService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WashRecordServiceImpl implements WashRecordService {

    @Autowired
    private WashRecordDao washRecordDao;

    @Autowired
    private WashEventDao washEventDao;

    @Override
    public void save(WashRecord washRecord) {
        washRecordDao.save(washRecord);
        washEventDao.updateWt(washRecord.getUi(),washRecord.getDdt());
    }

    @Override
    public IPageResultData<List<WashRecord>> list(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            endDateTime = LocalDateTime.now();
            startDateTime = endDateTime.minusDays(7);
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            endDateTime = startDateTime.plusMinutes(7);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            startDateTime = endDateTime.minusDays(7);
        }
        return washRecordDao.list(userId, startDateTime, endDateTime, lionPage);
    }
}
