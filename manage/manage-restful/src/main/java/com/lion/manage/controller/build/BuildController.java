package com.lion.manage.controller.build;

import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:19
 */
@RestController
@RequestMapping("/build")
@Validated
@Api(tags = {"建筑管理"})
public class BuildController extends BaseControllerImpl implements BaseController {
}
