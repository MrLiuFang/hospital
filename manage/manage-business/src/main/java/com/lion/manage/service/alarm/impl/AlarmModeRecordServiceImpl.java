package com.lion.manage.service.alarm.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.manage.dao.alarm.AlarmModeRecordDao;
import com.lion.manage.entity.alarm.AlarmModeRecord;
import com.lion.manage.entity.alarm.vo.ListAlarmModeRecordVo;
import com.lion.manage.service.alarm.AlarmModeRecordService;
import com.lion.manage.utils.ExcelColumn;
import com.lion.manage.utils.ExportExcelUtil;
import com.lion.upms.entity.enums.AlarmMode;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/26 下午8:08
 */
@Service
public class AlarmModeRecordServiceImpl extends BaseServiceImpl<AlarmModeRecord> implements AlarmModeRecordService {

    @Autowired
    private AlarmModeRecordService alarmModeRecordService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private AlarmModeRecordDao alarmModeRecordDao;

    @Autowired
    private HttpServletResponse response;

    @Override
    public IPageResultData<List<ListAlarmModeRecordVo>> list(LocalDateTime startDateTime, LocalDateTime endDateTime, AlarmMode alarmMode, String name, LionPage lionPage) {
        List<Long> userIds = new ArrayList<>();
        if (StringUtils.hasText(name)) {
            List<User> users = userExposeService.findByName(name);
            userIds.add(Long.MAX_VALUE);
            users.forEach(user->{
                userIds.add(user.getId());
            });
        }

        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(startDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.GREATER_THAN_OR_EQUAL_TO+"_createDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)){
            jpqlParameter.setSearchParameter(SearchConstant.LESS_THAN_OR_EQUAL_TO+"_createDateTime",endDateTime);
        }
        if (Objects.nonNull(alarmMode)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_alarmMode",alarmMode);
        }
        if (Objects.nonNull(userIds) && userIds.size()>0) {
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_userId",userIds);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<AlarmModeRecord> page = alarmModeRecordService.findNavigator(lionPage);
        List<AlarmModeRecord> list = page.getContent();
        List<ListAlarmModeRecordVo> returnList = new ArrayList<>();
        list.forEach(alarmModeRecord -> {
            ListAlarmModeRecordVo vo = new ListAlarmModeRecordVo();
            BeanUtils.copyProperties(alarmModeRecord,vo);
            com.lion.core.Optional<User> optional = userExposeService.findById(alarmModeRecord.getUserId());
            if (optional.isPresent()) {
                User user = optional.get();
                vo.setName(user.getName());
                vo.setHeadPortrait(user.getHeadPortrait());
                vo.setHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,page.getTotalElements());
    }

    @Override
    public void export(LocalDateTime startDateTime, LocalDateTime endDateTime, AlarmMode alarmMode, String name, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListAlarmModeRecordVo>> pageResultData = list(startDateTime,endDateTime,alarmMode,name,lionPage);
        List<ListAlarmModeRecordVo> list = pageResultData.getData();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        list.forEach(listAlarmModeRecordVo -> {
            listAlarmModeRecordVo.setDateTime(dateTimeFormatter.format(listAlarmModeRecordVo.getCreateDateTime()));
        });
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("時間", "dateTime"));
        excelColumn.add(ExcelColumn.build("操作員", "name"));
        excelColumn.add(ExcelColumn.build("模式", "alarmMode"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("alarmMode.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }
}
