package com.lion.person.service.person.impl;

import com.lion.aop.PageRequestInjection;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.department.Department;
import com.lion.person.dao.person.PatientReportDao;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.PatientTransfer;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        User user = userExposeService.find(addPatientReportDto.getNumber());
        if (Objects.isNull(user)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("1000033"));
        }
        patientReport.setReportUserId(user.getId());
        patientReport = save(patientReport);
        patientLogService.add("添加汇报",patientReport.getPatientId());
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
            User user = userExposeService.findById(vo.getReportUserId());
            if (Objects.nonNull(user)) {
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
            PatientReport patientReport = findById(deleteDto.getId());
            if (!Objects.equals(userId,patientReport.getReportUserId())){
                BusinessException.throwException(MessageI18nUtil.getMessage("1000034"));
            }
        });

        deleteDtoList.forEach(deleteDto -> {
            PatientReport patientReport = findById(deleteDto.getId());
            deleteById(deleteDto.getId());
            patientLogService.add("删除汇报",patientReport.getPatientId());
        });
    }
}
