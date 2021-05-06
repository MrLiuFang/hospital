package com.lion.event.controller;

import com.lion.common.constants.RedisConstants;
import com.lion.common.dto.UserCurrentRegionDto;
import com.lion.common.utils.RedisUtil;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.event.entity.DeviceData;
import com.lion.event.entity.Wash;
import com.lion.event.entity.vo.UserCurrentRegionVo;
import com.lion.event.service.DeviceDataService;
import com.lion.event.service.WashService;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午10:19
 **/
@RestController
@RequestMapping()
@Validated
@Api(tags = {"事件数据"})
public class EventDataController extends BaseControllerImpl implements BaseController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WashService washService;

    @Autowired
    private DeviceDataService deviceDataService;

    @GetMapping("/user/current/region")
    @ApiOperation(value = "用户当前位置")
    public IResultData<UserCurrentRegionVo> userCurrentRegionVo(@ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId) {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+userId);
        if (Objects.nonNull(userCurrentRegionDto)){
            UserCurrentRegionVo vo = new UserCurrentRegionVo();
            vo.setFirstEntryTime(userCurrentRegionDto.getFirstEntryTime());
            Region region = redisUtil.getRegionById(userCurrentRegionDto.getRegionId());
            if (Objects.nonNull(region)) {
                vo.setRegionId(region.getId());
                vo.setRegionName(region.getName());
                Build build = redisUtil.getBuild(region.getBuildId());
                if (Objects.nonNull(build)) {
                    vo.setBuildId(build.getId());
                    vo.setBuildName(build.getName());
                }
                BuildFloor buildFloor = redisUtil.getBuildFloor(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)) {
                    vo.setBuildFloorId(buildFloor.getId());
                    vo.setBuildFloorName(buildFloor.getName());
                }
                Department department = redisUtil.getDepartment(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    vo.setDepartmentId(department.getId());
                    vo.setDepartmentName(department.getName());
                }
            }
            return ResultData.instance().setData(vo);
        }
        return ResultData.instance();
    }

    @GetMapping("/wash/list")
    @ApiOperation(value = "用户洗手记录(不返回总行数，数据量大查询总行数费时，不给时间范围默认查询一周内的数据，以提高性能)")
    public IPageResultData<List<Wash>> washList( @ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId,
                                                            @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                            @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                            LionPage lionPage) {
        return washService.list(userId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/star/data/list")
    @ApiOperation(value = "star记录(不返回总行数，数据量大查询总行数费时，不给时间范围默认查询一周内的数据，以提高性能)")
    public IPageResultData<List<DeviceData>> starList(@ApiParam(value = "starid") @NotNull(message = "starid不能为空") Long starId,
                                                      @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return deviceDataService.list(starId, startDateTime, endDateTime, lionPage);
    }
}
