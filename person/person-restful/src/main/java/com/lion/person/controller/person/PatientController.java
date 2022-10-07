package com.lion.person.controller.person;

import com.lion.core.*;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.device.expose.tag.TagPatientExposeService;
import com.lion.person.entity.enums.TransferState;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.dto.*;
import com.lion.person.entity.person.vo.*;
import com.lion.person.service.person.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
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

    @Autowired
    private PatientReportService patientReportService;

    @Autowired
    private PatientLogService patientLogService;

    @DubboReference
    private TagPatientExposeService tagPatientExposeService;

    @PostMapping("/add")
    @ApiOperation(value = "新增患者")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddPatientDto addPatientDto){
        patientService.add(addPatientDto);
        return ResultData.instance();
    }

    @GetMapping({"/list/merge"})
    @ApiOperation("患者,流动人员合并查询")
    public IPageResultData<List<ListMergeVo>> listMerge(@ApiParam("类型：1=患者，2=流动人员，不传查所有") Integer type, @ApiParam("姓名") String name, @ApiParam("金卡号") String cardNumber, @ApiParam("标签编码") String tagCode, @ApiParam("病历号") String medicalRecordNo,@ApiParam("排序字段逗号隔开(name,tagCode)") String sort, LionPage lionPage) {
        return this.patientService.listMerge(type, name, cardNumber, tagCode, medicalRecordNo,sort , lionPage);
    }

    @GetMapping("/list")
    @ApiOperation(value = "患者列表")
    public IPageResultData<List<ListPatientVo>> list(@ApiParam(value = "患者级别")Integer level, @ApiParam(value = "重复数据是否只显示一条数据")Boolean isOne, @ApiParam(value = "床位编码")String bedCode, @ApiParam(value = "关键字")String keyword, @ApiParam(value = "姓名")String name, @ApiParam(value = "是否登出（true=历史患者）") Boolean isLeave, @ApiParam(value = "是否等待登出(通过回收箱登出)") Boolean isWaitLeave, @ApiParam(value = "出生日期(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
                                                     @ApiParam(value = "转移状态") TransferState transferState, @ApiParam(value = "金卡号")String cardNumber, @ApiParam(value = "标签编码") String tagCode, @ApiParam(value = "病历号") String medicalRecordNo, @ApiParam(value = "床位id") Long sickbedId,
                                                     @ApiParam(value = "入院开始时间(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDateTime, @ApiParam(value = "入院结束时间(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDateTime,
                                                     LionPage lionPage){
        return (IPageResultData<List<ListPatientVo>>) patientService.list(level, isOne, bedCode, keyword, name, isLeave, isWaitLeave, birthday, transferState, tagCode, medicalRecordNo, sickbedId, startDateTime, endDateTime, cardNumber, lionPage);
    }

    @GetMapping("/details")
    @ApiOperation(value = "患者详情")
    public IResultData<PatientDetailsVo> details(@NotNull(message = "{0000000}") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(patientService.details(id));
        return resultData;
    }

    @GetMapping("/details/cardNumber")
    @ApiOperation(value = "根据金卡号查询最后一次登记记录")
    public IResultData<PatientDetailsVo> detailsCardNumber(@ApiParam(value = "金卡号")String cardNumber){
        ResultData resultData = ResultData.instance();
        resultData.setData(patientService.detailsCardNumber(cardNumber));
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
    @ApiOperation(value = "患者登出(修改是否登出状态)")
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

    @DeleteMapping("/transfer/delete")
    @ApiOperation(value = "患者转移")
    public IResultData transfer(@RequestBody List<DeleteDto> deleteDtoList){
        deleteDtoList.forEach(deleteDto -> {
            patientTransferService.deleteById(deleteDto.getId());
        });
        ResultData resultData = ResultData.instance();
        return resultData;
    }


    @PutMapping("/transfer/update")
    @ApiOperation(value = "修改患者转移状态(只修改非完成&取消的转移)")
    public IResultData transferUpdate(@RequestBody @Validated UpdateTransferDto updateTransferDto){
        patientTransferService.updateState(updateTransferDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/transfer/list")
    @ApiOperation(value = "患者转移记录")
    public IPageResultData<List<ListPatientTransferVo>> transferList(@ApiParam(value = "患者id") Long patientId,LionPage lionPage ){
       return (IPageResultData<List<ListPatientTransferVo>>) patientTransferService.list(patientId, lionPage);
    }

    @PostMapping("/receiveOrCancel")
    @ApiOperation(value = "接收/取消转移患者(取消转移不修改病人信息-床位，负责医生……等)只修改状态")
    public IResultData receiveOrCancel(@RequestBody @Validated({Validator.OtherOne.class}) ReceivePatientDto receivePatientDto) {
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
    @ApiOperation(value = "提前结束临时离开权限(取消/返回)")
    public IResultData advanceOverTempLeave(@RequestBody @Validated AdvanceOverTempLeaveDto advanceOverTempLeaveDto) {
        tempLeaveService.advanceOverTempLeave(advanceOverTempLeaveDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/temp/leave/list")
    @ApiOperation(value = "临时离开列表")
    public IPageResultData<List<ListTempLeaveVo>> tempLeaveList(@ApiParam(value = "标签编码") String tagCode,@ApiParam(value = "科室id") Long departmentId,@ApiParam(value = "患者id") Long patientId, @ApiParam(value = "登记人id") Long userId, @ApiParam(value = "开始离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "结束离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,String ids, LionPage lionPage){
        return tempLeaveService.list(tagCode, departmentId, patientId, userId, startDateTime, endDateTime,ids , lionPage);
    }

    @GetMapping("/temp/leave/list/export")
    @ApiOperation(value = "临时离开列表导出")
    public void tempLeaveListExport(@ApiParam(value = "标签编码") String tagCode,@ApiParam(value = "科室id") Long departmentId,@ApiParam(value = "患者id") Long patientId, @ApiParam(value = "登记人id") Long userId, @ApiParam(value = "开始离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime, @ApiParam(value = "结束离开时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, String ids,LionPage lionPage) throws IOException, IllegalAccessException {
        tempLeaveService.export(tagCode, departmentId, patientId, userId, startDateTime, endDateTime, ids, lionPage);
    }

    @PostMapping("/report/add")
    @ApiOperation(value = "添加医护汇报")
    public IResultData addReport(@RequestBody @Validated({Validator.Insert.class}) AddPatientReportDto addPatientReportDto) {
        patientReportService.add(addPatientReportDto);
        return ResultData.instance();
    }

    @GetMapping("/report/list")
    @ApiOperation(value = "医护汇报列表")
    public IPageResultData<List<ListPatientReportVo>> listReport(@ApiParam(value = "患者id") @NotNull(message = "{1000026}") Long patientId,LionPage lionPage) {
        return patientReportService.list(patientId, lionPage);
    }

    @DeleteMapping("/report/delete")
    @ApiOperation(value = "删除医护汇报")
    public IResultData deleteReport(@RequestBody List<DeleteDto> deleteDtoList) {
        patientReportService.delete(deleteDtoList);
        return ResultData.instance();
    }

    @GetMapping("/log/list")
    @ApiOperation(value = "患者日志")
    public IPageResultData<List<ListPatientLogVo>> listLog(@ApiParam(value = "患者id") @NotNull(message = "{1000026}") Long patientId,LionPage lionPage) {
        return patientLogService.list(patientId, lionPage);
    }

    @GetMapping({"/today/statistics"})
    @ApiOperation("今日登记和登出统计")
    public IResultData<TodayStatisticsVo> todayStatistics() {
        return ResultData.instance().setData(this.patientService.todayStatistics());
    }

    @GetMapping({"/unbundlingTag"})
    @ApiOperation("患者解绑tag")
    public IResultData unbundlingTag(Long id){
        Optional<Patient> optional = patientService.findById(id);
        optional.ifPresent(patient -> {
            patient.setTagCode("");
            patientService.update(patient);
        });
        tagPatientExposeService.unbinding(id,false);
//        patientTransferService.deleteByPatientId(id);
//        PatientTransfer patientTransfer = new PatientTransfer();
//        patientTransfer.setPatientId(id);
//        patientTransfer.setState(TransferState.WAITING_TO_RECEIVE);
//        patientTransferService.save(patientTransfer);

        UpdateTransferDto updateTransferDto = new UpdateTransferDto();
        updateTransferDto.setPatientId(id);
        updateTransferDto.setTransferState(TransferState.WAITING_TO_RECEIVE);
        patientTransferService.updateState(updateTransferDto);
        return ResultData.instance();
    }
}
