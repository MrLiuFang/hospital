package com.lion.manage.controller.ward;

import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:21
 */
@RestController
@RequestMapping("/ward")
@Validated
@Api(tags = {"病房管理"})
public class WardController extends BaseControllerImpl implements BaseController {
}
