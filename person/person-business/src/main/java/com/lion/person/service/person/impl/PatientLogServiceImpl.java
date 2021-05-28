package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.PatientLogDao;
import com.lion.person.entity.person.PatientLog;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.vo.ListPatientLogVo;
import com.lion.person.entity.person.vo.ListPatientReportVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:32
 */
@Service
public class PatientLogServiceImpl extends BaseServiceImpl<PatientLog> implements PatientLogService {

    @Autowired
    private PatientLogDao patientLogDao;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;


    @Override
    public void add(String content, Long patientId) {
        PatientLog patientLog = new PatientLog();
        patientLog.setContent(content);
        patientLog.setPatientId(patientId);
        save(patientLog);
    }

    @Override
    public IPageResultData<List<ListPatientLogVo>> list(Long patientId, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(patientId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_patientId",patientId);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<PatientLog> page = findNavigator(lionPage);
        List<PatientLog> list = page.getContent();
        List<ListPatientLogVo> returnList = new ArrayList<>();
        list.forEach(patientLog -> {
            ListPatientLogVo vo = new ListPatientLogVo();
            BeanUtils.copyProperties(patientLog,vo);
            User user = userExposeService.findById(vo.getCreateUserId());
            if (Objects.nonNull(user)) {
                vo.setUserName(user.getName());
                vo.setUserHeadPortrait(user.getHeadPortrait());
                vo.setUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }
}
