package com.lion.person.controller.person;

import cn.hutool.core.bean.BeanUtil;
import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.person.entity.enums.ConfigureType;
import com.lion.person.entity.person.PersonFieldConfigure;
import com.lion.person.entity.person.dto.AddPatientDto;
import com.lion.person.service.person.PersonFieldConfigureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/23 下午3:05
 */
@RestController
@RequestMapping("/person/field/configure")
@Validated
@Api(tags = {"人员信息字段配置"})
public class PersonFieldConfigureController extends BaseControllerImpl implements BaseController {

    @Autowired
    private PersonFieldConfigureService personFieldConfigureService;

    @PostMapping("/save")
    @ApiOperation(value = "保存配置")
    @ApiImplicitParams(value = {@ApiImplicitParam(name="id",value="id"),@ApiImplicitParam(name="version",value="version"),@ApiImplicitParam(name="conetnt",value="配置项-json格式传送")})
    public IResultData add(@RequestBody Map<String,Object> map){
        PersonFieldConfigure personFieldConfigure = new PersonFieldConfigure();
        BeanUtil.fillBeanWithMap(map, personFieldConfigure.getClass(),true);
        personFieldConfigureService.update(personFieldConfigure);
        return ResultData.instance();
    }

    @GetMapping("/info")
    @ApiOperation(value = "获取配置(遍历content内容显示)")
    public IResultData<PersonFieldConfigure> add(@NotNull(message = "{1000051}") ConfigureType configureType){
        return ResultData.instance().setData(personFieldConfigureService.find(configureType));
    }
}
