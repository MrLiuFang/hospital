package com.lion.device.expose.tag;

import com.lion.core.service.BaseService;
import com.lion.device.entity.tag.TagPatient;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:34
 */
public interface TagPatientExposeService extends BaseService<TagPatient> {

    /** 患者与标签绑定
     * @param patientId
     * @param tagCode
     * @param departmentId
     */
    public void binding(Long patientId, String tagCode, Long departmentId);

    /**
     * 患者与标签解绑
     * @param patientId
     * @param isDelete
     */
    public void unbinding(Long patientId,Boolean isDelete);

    /**
     * 根据标签ID查询关联关系
     * @param tagId
     * @return
     */
    public TagPatient find(Long tagId);
}
