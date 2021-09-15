package com.lion.manage.controller.rule;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.manage.entity.rule.dto.AddWashTemplateDto;
import com.lion.manage.entity.rule.dto.UpdateWashTemplateDto;
import com.lion.manage.entity.rule.vo.DetailsWashTemplateVo;
import com.lion.manage.entity.rule.vo.ListWashTemplateVo;
import com.lion.manage.service.rule.WashDeviceTypeService;
import com.lion.manage.service.rule.WashTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/9下午5:02
 */
@RestController
@RequestMapping("/rule/wash")
@Validated
@Api(tags = {"洗手规则"})
public class WashController extends BaseControllerImpl implements BaseController {

//    @Autowired
//    private WashService washService;
//
//    @Autowired
//    private WashDeviceService washDeviceService;

    @Autowired
    private WashDeviceTypeService washDeviceTypeService;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @Autowired
    private WashTemplateService washTemplateService;

//    @PostMapping("/add")
//    @ApiOperation(value = "新增洗手规则")
//    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddWashDto addWashDto){
//        washService.add(addWashDto);
//        return ResultData.instance();
//    }
//
//
//    @GetMapping("/list")
//    @ApiOperation(value = "洗手规则列表")
//    public IPageResultData<List<ListWashVo>> list(@ApiParam(value = "名称") String name,@ApiParam(value = "洗手规则类型") WashRuleType type, LionPage lionPage){
//        ResultData resultData = ResultData.instance();
//        JpqlParameter jpqlParameter = new JpqlParameter();
//        if (StringUtils.hasText(name)){
//            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
//        }
//        if (Objects.nonNull(type)) {
//            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
//        }
//        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
//        lionPage.setJpqlParameter(jpqlParameter);
//        PageResultData page = (PageResultData) washService.findNavigator(lionPage);
//        List<Wash> list = page.getContent();
//        List<ListWashVo> returnList = new ArrayList<>();
//        list.forEach(wash -> {
//            ListWashVo vo = new ListWashVo();
//            BeanUtils.copyProperties(wash,vo);
//            List<WashDevice> washDevices = washDeviceService.find(wash.getId());
//            if (Objects.nonNull(washDevices) && washDevices.size()>0){
//                List<Device> deviceList = new ArrayList<>();
//                washDevices.forEach(washDevice -> {
//                    Device device = deviceExposeService.findById(washDevice.getDeviceId());
//                    if (Objects.nonNull(device)) {
//                        deviceList.add(device);
//                    }
//                });
//                vo.setDevices(deviceList);
//            }
//            vo.setWashDeviceTypes(washDeviceTypeService.find(wash.getId()));
//            returnList.add(vo);
//        });
//        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
//    }
//
//    @GetMapping("/details")
//    @ApiOperation(value = "洗手规则详情")
//    public IResultData<DetailsWashVo> details(@NotNull(message = "{0000000}") Long id){
//        ResultData resultData = ResultData.instance();
//        resultData.setData(washService.details(id));
//        return resultData;
//    }
//
//    @PutMapping("/update")
//    @ApiOperation(value = "修改洗手规则")
//    public IResultData update(@RequestBody @Validated({Validator.Update.class}) UpdateWashDto updateWashDto){
//        washService.update(updateWashDto);
//        return ResultData.instance();
//    }
//
//    @ApiOperation(value = "删除洗手规则")
//    @DeleteMapping("/delete")
//    public IResultData delete(@RequestBody List<DeleteDto> deleteDtoList){
//        washService.delete(deleteDtoList);
//        ResultData resultData = ResultData.instance();
//        return resultData;
//    }

    @PostMapping("/add/template")
    @ApiOperation(value = "新增洗手规则模板")
    public IResultData addTemplate(@RequestBody @Validated({Validator.Insert.class})AddWashTemplateDto addWashTemplateDto) {
        washTemplateService.add(addWashTemplateDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @PutMapping("/update/template")
    @ApiOperation(value = "修改洗手规则模板")
    public IResultData updateTemplate(@RequestBody @Validated({Validator.Update.class}) UpdateWashTemplateDto updateWashTemplateDto) {
        washTemplateService.update(updateWashTemplateDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @DeleteMapping("/delete/template")
    @ApiOperation(value = "删除洗手规则模板")
    public IResultData deleteTemplate(@RequestBody List<DeleteDto> deleteDtoList) {
        washTemplateService.delete(deleteDtoList);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/list/template")
    @ApiOperation(value = "洗手规则模板列表")
    public IPageResultData<List<ListWashTemplateVo>> listTemplate(String name, LionPage lionPage) {
        return washTemplateService.list(name, lionPage);
    }

    @GetMapping("/details/template")
    @ApiOperation(value = "洗手规则模板详情")
    public IResultData<DetailsWashTemplateVo> detailsTemplate(@ApiParam(value = "类型id") @NotNull(message = "{0000000}") Long id) {
        return ResultData.instance().setData(washTemplateService.details(id));
    }
}
