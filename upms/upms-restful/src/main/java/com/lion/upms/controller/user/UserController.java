package com.lion.upms.controller.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.common.enums.ResultDataState;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.core.persistence.Validator;
import com.lion.upms.entity.user.User;
import com.lion.upms.entity.user.dto.AddUserDto;
import com.lion.upms.entity.user.vo.DetailsUserVo;
import com.lion.upms.entity.user.vo.ListUserVo;
import com.lion.upms.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description: 用户控制层
 * @date 2021/3/22下午9:22
 */
@RestController
@RequestMapping("/user/")
@Validated
@Api(tags = {"用户管理"})
public class UserController extends BaseControllerImpl implements BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @ApiOperation(value = "新增用户",notes = "新增用户")
    public IResultData add(@RequestBody @Validated({Validator.Insert.class}) AddUserDto addUserDto){
        userService.add(addUserDto);
        ResultData resultData = ResultData.instance();
        return resultData;
    }

    @GetMapping("/list")
    @ApiOperation(value = "用户列表",notes = "用户列表")
    public IPageResultData<List<ListUserVo>> list(@ApiParam(value = "搜索关键词") String keyword, LionPage lionPage){
        return userService.list(keyword, lionPage);
    }


    @GetMapping("/details")
    @ApiOperation(value = "用户详情",notes = "用户详情")
    public IResultData<DetailsUserVo> details(@NotNull(message = "id不能为空") Long id){
        ResultData resultData = ResultData.instance();
        resultData.setData(userService.details(id));
        return resultData;
    }
}
