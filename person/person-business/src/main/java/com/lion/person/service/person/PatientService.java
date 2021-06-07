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
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;

import java.time.LocalDateTime;
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
     * @param name
     * @param isLeave
     * @param isWaitLeave
     * @param birthday
     * @param transferState
     * @param isNormal
     * @param tagCode
     * @param medicalRecordNo
     * @param sickbedId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListPatientVo>> list(String name, Boolean isLeave, Boolean isWaitLeave,LocalDateTime birthday,TransferState transferState,Boolean isNormal,String tagCode,String medicalRecordNo, Long sickbedId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);

    /**
     * 详情
     * @param id
     * @return
     */
    public PatientDetailsVo details(Long id);

    /**
     * 患者登出
     * @param patientLeaveDto
     */
    public void leave(PatientLeaveDto patientLeaveDto);


}
