package com.lion.person.service.person;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientLog;
import com.lion.person.entity.person.vo.ListPatientLogVo;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/28 下午3:30
 */
public interface PatientLogService extends BaseService<PatientLog> {

    /**
     * 添加日志
     * @param content
     * @param patientId
     */
    public void add(String content,Long patientId);

    /**
     * 列表
     * @param patientId
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListPatientLogVo>> list(Long patientId, LionPage lionPage);
}
