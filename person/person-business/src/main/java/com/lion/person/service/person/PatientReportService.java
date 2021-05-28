package com.lion.person.service.person;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientReport;
import com.lion.person.entity.person.dto.AddPatientReportDto;
import com.lion.person.entity.person.vo.ListPatientReportVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:29
 */
public interface PatientReportService extends BaseService<PatientReport> {

    /**
     * 添加汇报
     * @param addPatientReportDto
     */
    public void add(AddPatientReportDto addPatientReportDto);

    /**
     *
     * @param patientId
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListPatientReportVo>> list(Long patientId, LionPage lionPage);

    /**
     * 删除医护汇报
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
