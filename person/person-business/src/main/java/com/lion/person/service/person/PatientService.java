package com.lion.person.service.person;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;

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
}
