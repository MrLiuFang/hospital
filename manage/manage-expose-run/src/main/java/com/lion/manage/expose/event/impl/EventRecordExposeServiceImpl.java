package com.lion.manage.expose.event.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.event.EventRecordDao;
import com.lion.manage.entity.event.EventRecord;
import com.lion.manage.entity.event.vo.EventRecordVo;
import com.lion.manage.expose.event.EventRecordExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/29 上午10:13
 */
@DubboService(interfaceClass = EventRecordExposeService.class)
public class EventRecordExposeServiceImpl extends BaseServiceImpl<EventRecord> implements EventRecordExposeService {

    @Autowired
    private EventRecordDao eventRecordDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;


    @Override
    public EventRecord add(String code, String remarks, String content, String extend, String url) {
        EventRecord eventRecord = new EventRecord();
        eventRecord.setRemarks(remarks);
        eventRecord.setContent(content);
        eventRecord.setCode(code);
        eventRecord.setExtend(extend);
        eventRecord = save(eventRecord);
        return eventRecord;
    }

    @Override
    public IPageResultData<List<EventRecordVo>> list(LocalDateTime startDatetime, LocalDateTime endDateTime, String code, String name, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> userIds = new ArrayList<Long>();
        if (StringUtils.hasText(name)) {
            userIds.add(Long.MAX_VALUE);
            List<User> list = userExposeService.findByName(name);
            list.forEach(user -> {
                userIds.add(user.getId());
            });
        }
        if (userIds.size()>0) {
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_createUserId",userIds);
        }
        if (StringUtils.hasText(code)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (Objects.nonNull(startDatetime)) {
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",startDatetime);
        }
        if (Objects.nonNull(endDateTime)) {
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",startDatetime);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<EventRecord> page = this.findNavigator(lionPage);
        List<EventRecord> list = page.getContent();
        List<EventRecordVo> recordVos = new ArrayList<>();
        list.forEach(eventRecord -> {
            EventRecordVo vo = new EventRecordVo();
            BeanUtils.copyProperties(eventRecord,vo);
            com.lion.core.Optional<User> optional = userExposeService.findById(eventRecord.getCreateUserId());
            if (optional.isPresent()) {
                User user = optional.get();
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setCreateUserName(user.getName());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            recordVos.add(vo);
        });
        return new PageResultData<>(recordVos,lionPage,page.getTotalElements());
    }

    @Override
    public EventRecordVo details(Long id) {
        com.lion.core.Optional<EventRecord> optional = this.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        EventRecord eventRecord = optional.get();
        com.lion.core.Optional<User> optionalUser = userExposeService.findById(eventRecord.getCreateUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            EventRecordVo vo = new EventRecordVo();
            BeanUtils.copyProperties(eventRecord,vo);
            vo.setHeadPortrait(user.getHeadPortrait());
            vo.setCreateUserName(user.getName());
            vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            return vo;
        }
        return null;
    }
}
