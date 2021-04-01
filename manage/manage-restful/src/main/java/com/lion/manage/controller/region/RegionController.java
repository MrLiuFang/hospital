package com.lion.manage.controller.region;

import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:20
 */
@RestController
@RequestMapping("/region")
@Validated
@Api(tags = {"区域管理"})
public class RegionController extends BaseControllerImpl implements BaseController {
}
