package com.lion.person.controller.person;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.dto.*;
import com.lion.person.entity.person.vo.ListPatientVo;
import com.lion.person.entity.person.vo.ListTempLeaveVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.service.person.PatientService;
import com.lion.person.service.person.PatientTransferService;
import com.lion.person.service.person.TempLeaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:16
 */
@RestController
@RequestMapping("/patient")
@Validated
@Api(tags = {"患者"})
public class PatientController extends BaseControllerImpl implements BaseController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientTransferService patientTransferService;

    @Autowired
    private TempLeaveService tempLeaveService;

    @PostMapping("/add")
    @ApiOperation(value = "新增患者")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddPatientDto addPatientDto){
        patientService.add(addPatientDto);
        return ResultData.instance();
    }


    @GetMapping("/list")
    @ApiOperation(value = "患者列表")
    public IPageResultData<List<ListPatientVo>> list(@ApiParam(value = "姓名")String name, @ApiParam(value = "是否登出（true=历史患者）") Boolean isLeave,@ApiParam(value = "出生日期(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime birthday,
                                                     @ApiParam(value = "转移状态") TransferState transferState, @ApiParam(value = "状态）") Boolean isNormal,@ApiParam(value = "标签编码") String tagCode,@ApiParam(value = "病历号") String medicalRecordNo,@ApiParam(value = "床位id") Long sickbedId,
                                                     @ApiParam(value = "入院开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "入院结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage){
        return patientService.list(name, isLeave, birthday, transferState, isNormal, tagCode, medicalRecordNo, sickbedId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "患者详情")
    public IResultData<PatientDetailsVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(patientService.details(id));
        return resultData;
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改患者")
    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdatePatientDto updatePatientDto){
        patientService.update(updatePatientDto);
        return ResultData.instance();
    }

    @ApiOperation(value = "删除患者")
    @DeleteMapping("/delete")
    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
        patientService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PutMapping("/leave")
    @ApiOperation(value = "患者登出")
    public IResultData leave(@RequestBody @Validated PatientLeaveDto patientLeaveDto){
        patientService.leave(patientLeaveDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/transfer")
    @ApiOperation(value = "患者转移")
    public IResultData transfer(@RequestBody @Validated TransferDto transferDto){
        patientTransferService.transfer(transferDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/transfer/list")
    @ApiOperation(value = "患者转移")
    public IResultData<List<>> transferList(@ApiParam(value = "患者id") Long patientId ){
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/receiveOrCancel")
    @ApiOperation(value = "接收/取消传其患者(本接口修改患者转移状态,其它数据调患者修改接口,接收患者需要调两次接口（患者修改和本接口）)")
    public IResultData receiveOrCancel(@RequestBody @Validated ReceivePatientDto receivePatientDto) {
        patientTransferService.receiveOrCancel(receivePatientDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PostMapping("/add/temp/leave")
    @ApiOperation(value = "新增临时离开")
    public IResultData addTempLeave(@RequestBody @Validated AddTempLeaveDto addTempLeaveDto){
        tempLeaveService.addTempLeave(addTempLeaveDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PutMapping("/advance/over/temp/leave")
    @ApiOperation(value = "提前结束临时离开权限")
    public IResultData advanceOverTempLeave(@RequestBody @Validated AdvanceOverTempLeaveDto advanceOverTempLeaveDto) {
        tempLeaveService.advanceOverTempLeave(advanceOverTempLeaveDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/temp/leave/list")
    @ApiOperation(value = "临时离开列表")
    public IPageResultData<List<ListTempLeaveVo>> tempLeaveList(@ApiParam(value = "患者id") Long patientId, @ApiParam(value = "登记人id") Long userId, @ApiParam(value = "开始离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "结束离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return tempLeaveService.list(patientId, userId, startDateTime, endDateTime, lionPage);
    }

}
