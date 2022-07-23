package com.lion.person.dao.person;

import com.lion.core.LionPage;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PatientDaoEx {

    Page<Map<String, Object>> listMerge(Integer type, String name, String cardNumber, String tagCode, String medicalRecordNo, String sort, List<Long> departmentIds, LionPage lionPage);
}
