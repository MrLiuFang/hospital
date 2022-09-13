package com.lion.person.service.person;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.PatientLeaveDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.entity.person.vo.ListMergeVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.entity.person.vo.TodayStatisticsVo;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:06
 */
public interface PatientService extends BaseService<Patient> {

    /**
     * 新增患者
     * @param addPatientDto
     */
    public void add(AddPatientDto addPatientDto);

    /**
     * 修改患者
     * @param updatePatientDto
     */
    public void update(UpdatePatientDto updatePatientDto);

    /**
     * 删除
     * @param deleteDtos
     * @return
     */
    public void delete(List<DeleteDto> deleteDtos);

    /**
     * 列表
     *
     * @param level
     * @param isOne
     * @param bedCode
     * @param keyword
     * @param name
     * @param isLeave
     * @param isWaitLeave
     * @param birthday
     * @param transferState
     * @param tagCode
     * @param medicalRecordNo
     * @param sickbedId
     * @param startDateTime
     * @param endDateTime
     * @param cardNumber
     * @param lionPage
     * @return
     */
    public Page<Patient> list(Integer level,Boolean isOne, String bedCode, String keyword, String name, Boolean isLeave, Boolean isWaitLeave, LocalDate birthday, TransferState transferState, String tagCode, String medicalRecordNo, Long sickbedId, LocalDate startDateTime, LocalDate endDateTime, String cardNumber, LionPage lionPage);

    /**
     * 详情
     * @param id
     * @return
     */
    public PatientDetailsVo details(Long id);

    PatientDetailsVo detailsCardNumber(String cardNumber);

    /**
     * 患者登出
     * @param patientLeaveDto
     */
    public void leave(PatientLeaveDto patientLeaveDto);


    IPageResultData<List<ListMergeVo>> listMerge(Integer type,String name, String cardNumber, String tagCode, String medicalRecordNo,String sort, LionPage lionPage);

    TodayStatisticsVo todayStatistics();

    public Patient setOtherInfo(Patient patient);

    public void sickbedIsCanUse(Long sickbedId,Long patientId);
}
