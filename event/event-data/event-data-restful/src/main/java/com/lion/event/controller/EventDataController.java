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
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.DeviceData;
import com.lion.event.entity.WashRecord;
import com.lion.event.entity.vo.*;
import com.lion.event.service.*;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private WashRecordService washService;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private WashEventService eventService;

    @Autowired
    private SystemAlarmService systemAlarmService;

    @Autowired
    private CurrentPositionService currentPositionService;

    @Autowired
    private MapStatisticsService mapStatisticsService;

    @GetMapping("/user/current/region")
    @ApiOperation(value = "用户当前位置")
    public IResultData<UserCurrentRegionVo> userCurrentRegionVo(@ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId) {
        UserCurrentRegionDto userCurrentRegionDto = (UserCurrentRegionDto) redisTemplate.opsForValue().get(RedisConstants.USER_CURRENT_REGION+userId);
        if (Objects.isNull(userCurrentRegionDto)) {
            CurrentPosition currentPosition = currentPositionService.find(userId);
            if (Objects.nonNull(currentPosition)){
                userCurrentRegionDto = new UserCurrentRegionDto();
                userCurrentRegionDto.setUserId(userId);
                userCurrentRegionDto.setRegionId(currentPosition.getRi());
            }
        }

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
    public IPageResultData<List<WashRecord>> washList(@ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId,
                                                      @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return washService.list(userId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/star/data/list")
    @ApiOperation(value = "star记录(不返回总行数，数据量大查询总行数费时，不给时间范围默认查询一周内的数据，以提高性能)")
    public IPageResultData<List<DeviceData>> starList(@ApiParam(value = "starId") @NotNull(message = "starid不能为空") Long starId,
                                                      @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return deviceDataService.list(starId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/ratio")
    @ApiOperation(value = "手卫生监控（科室/全院合规率）")
    public IResultData<ListWashMonitorVo> washRatio(@NotNull(message = "开始时间不能为空") @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                    @NotNull(message = "结束时间不能为空") @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) {
        return ResultData.instance().setData(eventService.washRatio(startDateTime,endDateTime));
    }

    @GetMapping("/user/wash/below/standard")
    @ApiOperation(value = "手卫生监控（低于标准人员）不返回总行数")
    public IPageResultData<List<ListUserWashMonitorVo>> userWashBelowStandard(@ApiParam(value = "用户类型") @NotNull(message = "用户类型不能为空") UserType userType,
                                                                              @NotNull(message = "开始时间不能为空") @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                              @NotNull(message = "结束时间不能为空") @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                              LionPage lionPage) {
        return eventService.userWashRatio(userType, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/wash/details")
    @ApiOperation(value = "手卫生监控（用户洗手详细）不返回总行数")
    public IResultData<UserWashDetailsVo> userWashDetails(@ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId,
                                                                    @NotNull(message = "开始时间不能为空") @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                    @NotNull(message = "结束时间不能为空") @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                    LionPage lionPage) {
        return ResultData.instance().setData(eventService.userWashDetails(userId, startDateTime, endDateTime, lionPage));
    }

    @GetMapping("/user/wash/conformance/ratio")
    @ApiOperation(value = "手卫生监控（员工合规率）")
    public IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(@ApiParam(value = "用户姓名") String userName,@ApiParam(value = "部门id") Long departmentId,@ApiParam(value = "用户类型")  UserType userType,
                                                          @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                          @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                          LionPage lionPage) {
        return eventService.userWashConformanceRatio(userName,departmentId,userType,startDateTime,endDateTime,lionPage);
    }

    @PutMapping("/unalarm")
    @ApiImplicitParams({@ApiImplicitParam(value = "uuid")})
    @ApiOperation(value = "警告知熟(处理警告)")
    public IResultData unalarm(@RequestBody Map<String,String> map) {
        if (map.containsKey("uuid")) {
            systemAlarmService.unalarm(map.get("uuid"));
        }
        return ResultData.instance();
    }

    @GetMapping("/region/statistics/details")
    @ApiOperation(value = "地图监控地图统计（员工，患者，标签…………）")
    public IResultData<List<RegionStatisticsDetails>> regionStatisticsDetails(@ApiParam("楼层ID") @NotNull(message = "楼层ID不能为空") Long buildFloorId){
        return ResultData.instance().setData(mapStatisticsService.regionStatisticsDetails(buildFloorId));
    }

    @GetMapping("/department/statistics/details")
    @ApiOperation(value = "地图监控科室统计")
    public IResultData<List<DepartmentStatisticsDetails>> departmentStatisticsDetails() {
        return ResultData.instance().setData(mapStatisticsService.departmentStatisticsDetails());
    }

    @GetMapping("/department/staff/statistics/details")
    @ApiOperation(value = "地图监控科室员工统计")
    public IResultData<DepartmentStaffStatisticsDetails> departmentStaffStatisticsDetails(@ApiParam(value = "姓名") String name) {
        return ResultData.instance().setData(mapStatisticsService.departmentStaffStatisticsDetails(name));
    }

    @GetMapping("/department/assets/statistics/details")
    @ApiOperation(value = "地图监控科室资产统计")
    public IResultData<List<DepartmentAssetsStatisticsDetails>> departmentAssetsStatisticsDetails(@ApiParam(value = "名称/资产编码") String keyword) {
        return ResultData.instance().setData(mapStatisticsService.departmentAssetsStatisticsDetails(keyword));
    }
}
