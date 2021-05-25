package com.lion.person.controller.person;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.entity.person.dto.UpdatePatientDto;
import com.lion.person.service.person.PatientService;
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
import java.util.Objects;

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

    @PostMapping("/add")
    @ApiOperation(value = "新增患者")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddPatientDto addPatientDto){
        patientService.add(addPatientDto);
        return ResultData.instance();
    }


//    @GetMapping("/list")
//    @ApiOperation(value = "患者列表")
//    public IPageResultData<List<ListTagVo>> list(@ApiParam(value = "使用状态")TagUseState useState, @ApiParam(value = "电量(0=正常,1=少於90 天,2=少於30天)")Integer battery, @ApiParam(value = "标签编码") String tagCode, @ApiParam(value = "标签分类") TagType type, @ApiParam(value = "用途") TagPurpose purpose, LionPage lionPage){
//        return patientService.list(useState, battery, tagCode, type, purpose, lionPage);
//    }
//
//    @GetMapping("/details")
//    @ApiOperation(value = "患者详情")
//    public IResultData<Tag> details(@NotNull(message = "id不能为空") Long id){
//        ResultData resultData = ResultData.instance();
//        resultData.setData(patientService.findById(id));
//        return resultData;
//    }

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
}
