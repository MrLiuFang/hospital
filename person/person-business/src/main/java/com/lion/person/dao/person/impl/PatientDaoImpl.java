package com.lion.person.dao.person.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.person.dao.person.PatientDaoEx;
import com.lion.person.entity.person.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PatientDaoImpl implements PatientDaoEx {
    @Autowired
    private BaseDao<Patient> baseDao;

    public Page<Map<String, Object>> listMerge(Integer type, String name, String cardNumber, String tagCode, String medicalRecordNo, String sort, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap();
        if (StringUtils.hasText(name)) {
            searchParameter.put("name", "%" + name + "%");
        }
        if (StringUtils.hasText(tagCode)) {
            searchParameter.put("tagCode", "%" + tagCode + "%");
        }

        if ((Objects.nonNull(type) && Objects.equals(type,1)) ||  Objects.isNull(type)) {
            sb.append(" select id, 1 as 'type', head_portrait as 'headPortrait' , name,tag_code as 'tagCode', create_date_time as 'createDateTime' ,gender from t_patient where is_leave  <> 1  ");
            if (StringUtils.hasText(name)) {
                sb.append(" and name like :name ");
            }

            if (StringUtils.hasText(tagCode)) {
                sb.append(" and tag_code like :tagCode ");
            }

            if (StringUtils.hasText(cardNumber)) {
                sb.append(" and card_cumber like :cardNumber ");
                searchParameter.put("cardNumber", "%" + cardNumber + "%");
            }

            if (StringUtils.hasText(medicalRecordNo)) {
                sb.append(" and medical_record_no like :medicalRecordNo ");
                searchParameter.put("medicalRecordNo", "%" + medicalRecordNo + "%");
            }

        }
        if (!StringUtils.hasText(cardNumber) && !StringUtils.hasText(medicalRecordNo) && (Objects.nonNull(type) && Objects.equals(type,2) ||  Objects.isNull(type) )) {
            if (Objects.isNull(type)) {
                sb.append(" union ");
            }
            sb.append("  select id, 2 as 'type' , head_portrait as 'headPortrait' , name,tag_code as 'tagCode', create_date_time as 'createDateTime' ,gender from t_temporary_person where is_leave <> 1 ");
            if (StringUtils.hasText(name)) {
                sb.append(" and name like :name ");
            }

            if (StringUtils.hasText(tagCode)) {
                sb.append(" and tag_code like :tagCode ");
            }
        }

//        if (Objects.nonNull(sort)) {
//            String[] sorts= sort.split(",");
//            for (String s: sorts) {
//                sb.append(" and tag_code like :tagCode ");
//            }
//        }

        return (Page<Map<String, Object>>) this.baseDao.findNavigatorByNativeSql(lionPage, sb.toString(), searchParameter, HashMap.class);
    }
}
