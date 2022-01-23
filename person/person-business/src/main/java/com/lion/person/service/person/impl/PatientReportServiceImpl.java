package com.lion.person.service.person.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.person.dao.person.PatientReportDao;
import com.lion.person.entity.enums.LogType;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.dto.AddPatientReportDto;
import com.lion.person.entity.person.vo.ListPatientReportVo;
import com.lion.person.service.person.PatientLogService;
import com.lion.person.service.person.PatientReportService;
import com.lion.person.service.person.PatientService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:30
 */
@Service
public class PatientReportServiceImpl extends BaseServiceImpl<PatientReport> implements PatientReportService {

    @Autowired
    private PatientReportDao patientReportDao;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientLogService patientLogService;

    @DubboReference
    private UserExposeService userExposeService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Override
    @Transactional
    public void add(AddPatientReportDto addPatientReportDto) {
        PatientReport patientReport = new PatientReport();
        BeanUtils.copyProperties(addPatientReportDto,patientReport);
        com.lion.core.Optional<User> optional = userExposeService.findById(addPatientReportDto.getUserId());
        if (optional.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000033"));
        }
        patientReport.setReportUserId(optional.get().getId());
        patientReport = save(patientReport);
        patientLogService.add("",LogType.ADD_REPORT,CurrentUserUtil.getCurrentUserId() , patientReport.getPatientId());
    }

    @Override
    public IPageResultData<List<ListPatientReportVo>> list(Long patientId, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(patientId)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_patientId",patientId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page page = findNavigator(lionPage);
        List<PatientReport> list = page.getContent();
        List<ListPatientReportVo> returnList = new ArrayList<>();
        list.forEach(patientReport -> {
            ListPatientReportVo vo = new ListPatientReportVo();
            BeanUtils.copyProperties(patientReport,vo);
            com.lion.core.Optional<User> optional = userExposeService.findById(vo.getReportUserId());
            if (optional.isPresent()) {
                User user = optional.get();
                vo.setReportUserName(user.getName());
                vo.setReportUserHeadPortrait(user.getHeadPortrait());
                vo.setReportUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        Long userId = CurrentUserUtil.getCurrentUserId();
        deleteDtoList.forEach(deleteDto -> {
            com.lion.core.Optional<PatientReport> optional = findById(deleteDto.getId());
            if (optional.isPresent()) {
                if (!Objects.equals(userId,optional.get().getReportUserId())){
                    BusinessException.throwException(MessageI18nUtil.getMessage("1000034"));
                }
            }
        });

        deleteDtoList.forEach(deleteDto -> {
            com.lion.core.Optional<PatientReport> optional = findById(deleteDto.getId());
            if (optional.isPresent()) {
                deleteById(deleteDto.getId());
                patientLogService.add("", LogType.DELETE_REPORT, userId, optional.get().getPatientId());
            }
        });
    }
}
