package com.lion.person.service.person;

import com.lion.core.IResultData;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.PatientTransfer;
import com.lion.person.entity.person.dto.ReceivePatientDto;
import com.lion.person.entity.person.dto.TransferDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 下午5:32
 */
public interface PatientTransferService extends BaseService<PatientTransfer> {

    /**
     * 患者转移
     * @param transferDto
     * @return
     */
    public void transfer(TransferDto transferDto);

    /**
     * 接收/取消转移患者
     * @param receivePatientDto
     */
    public void receiveOrCancel(ReceivePatientDto receivePatientDto);
}
