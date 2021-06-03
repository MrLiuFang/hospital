package com.lion.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.utils.RedisUtil;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.event.entity.*;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.dto.OldAlarmToNewAlarm;
import com.lion.event.entity.dto.UnalarmDto;
import com.lion.event.entity.vo.*;
import com.lion.event.service.*;
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

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午10:19
 **/
@RestController
@Validated
@Api(tags = {"地图监控"})
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

    @Autowired
    private PositionService positionService;

    @GetMapping("/user/current/region")
    @ApiOperation(value = "员工当前位置")
    public IResultData<CurrentRegionVo> userCurrentRegion(@ApiParam(value = "用户id") @NotNull(message = "用户id不能为空") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.userCurrentRegion(userId));
    }

    @GetMapping("/patient/current/region")
    @ApiOperation(value = "患者当前位置")
    public IResultData<CurrentPosition> patientCurrentRegion(@ApiParam(value = "患者id") @NotNull(message = "患者id不能为空") Long patientId) {
        return ResultData.instance().setData(currentPositionService.find(patientId));
    }

    @GetMapping("/tag/current/region")
    @ApiOperation(value = "标签当前位置")
    public IResultData<CurrentPosition> tagCurrentPosition(@ApiParam(value = "标签id") @NotNull(message = "标签id不能为空") Long tagId) {
        return ResultData.instance().setData(currentPositionService.findByTagId(tagId));
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
    @ApiOperation(value = "警告知熟(处理警告)")
    public IResultData unalarm(@RequestBody UnalarmDto unalarmDto) throws JsonProcessingException {
        systemAlarmService.unalarm(unalarmDto.getUuid(),unalarmDto.getId());
        return ResultData.instance();
    }

    @PutMapping("/alarm/report")
    @ApiOperation(value = "添加汇报")
    public IResultData alarmReport(@RequestBody @Validated AlarmReportDto alarmReportDto) {
        systemAlarmService.alarmReport(alarmReportDto);
        return ResultData.instance();
    }

    @PutMapping("/oldAlarm/to/new")
    @ApiOperation(value = "历史警告添加为新的警告")
    public IResultData oldAlarmToNewAlarm(@RequestBody OldAlarmToNewAlarm oldAlarmToNewAlarm) throws JsonProcessingException {
        systemAlarmService.oldAlarmToNewAlarm(oldAlarmToNewAlarm.getId());
        return ResultData.instance();
    }

    @GetMapping("/region/statistics/details")
    @ApiOperation(value = "地图监控地图统计（员工，患者，标签…………）")
    public IResultData<List<RegionStatisticsDetails>> regionStatisticsDetails(@ApiParam("楼层ID") @NotNull(message = "楼层ID不能为空") Long buildFloorId){
        return ResultData.instance().setData(mapStatisticsService.regionStatisticsDetails(buildFloorId));
    }

    @GetMapping("/department/statistics/details")
    @ApiOperation(value = "地图监控科室统计(左边列表)")
    public IResultData<List<DepartmentStatisticsDetailsVo>> departmentStatisticsDetails() {
        return ResultData.instance().setData(mapStatisticsService.departmentStatisticsDetails());
    }

    @GetMapping("/department/staff/statistics/details")
    @ApiOperation(value = "地图监控科室员工统计(左边列表)")
    public IResultData<DepartmentStaffStatisticsDetailsVo> departmentStaffStatisticsDetails(@ApiParam(value = "姓名") String name) {
        return ResultData.instance().setData(mapStatisticsService.departmentStaffStatisticsDetails(name));
    }

    @GetMapping("/department/assets/statistics/details")
    @ApiOperation(value = "地图监控科室资产统计(左边列表)")
    public IResultData<DepartmentAssetsStatisticsDetailsVo> departmentAssetsStatisticsDetails(@ApiParam(value = "名称/资产编码") String keyword) {
        return ResultData.instance().setData(mapStatisticsService.departmentAssetsStatisticsDetails(keyword));
    }

    @GetMapping("/department/tag/statistics/details")
    @ApiOperation(value = "地图监控科室温标签统计(左边列表)")
    public IResultData<DepartmentTagStatisticsDetailsVo> departmentTagStatisticsDetails(@ApiParam(value = "名称/标签编码") String keyword) {
        return ResultData.instance().setData(mapStatisticsService.departmentTagStatisticsDetails(keyword));
    }

    @GetMapping("/department/patient/statistics/details")
    @ApiOperation(value = "地图监控患者统计(左边列表)")
    public IResultData<DepartmentPatientStatisticsDetailsVo> departmentPatientStatisticsDetails(@ApiParam(value = "姓名") String name) {
        return ResultData.instance().setData(mapStatisticsService.departmentPatientStatisticsDetails(name));
    }

    @GetMapping("/department/temporary/person/statistics/details")
    @ApiOperation(value = "地图监控流动人员统计(左边列表)")
    public IResultData<DepartmentTemporaryPersonStatisticsDetailsVo> departmentTemporaryPersonStatisticsDetails(@ApiParam(value = "姓名") String name) {
        return ResultData.instance().setData(mapStatisticsService.departmentTemporaryPersonStatisticsDetails(name));
    }


    @GetMapping("/patient/details")
    @ApiOperation(value = "地图监控患者详情")
    public IResultData<PatientDetailsVo> patientDetails(@ApiParam("患者id") @NotNull(message = "患者id不能为空") Long patientId) {
        return ResultData.instance().setData(mapStatisticsService.staffDetails(patientId));
    }

    @GetMapping("/staff/details")
    @ApiOperation(value = "地图监控员工详情")
    public IResultData<StaffDetailsVo> staffDetails(@ApiParam("员工id") @NotNull(message = "员工id不能为空") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.staffDetails(userId));
    }

    @GetMapping("/assets/position")
    @ApiOperation(value = "地图监控资产轨迹(不返回总行数)")
    public IPageResultData<List<Position>> assetsPosition(@ApiParam("资产id") @NotNull(message = "资产id不能为空") Long assetsId,LionPage lionPage) {
        return positionService.list(null,assetsId , lionPage);
    }

    @GetMapping("/staff/position")
    @ApiOperation(value = "地图监控员工轨迹(不返回总行数)")
    public IPageResultData<List<Position>> staffPosition(@ApiParam("员工id") @NotNull(message = "员工id不能为空") Long userId,LionPage lionPage) {
        return positionService.list(userId,null , lionPage);
    }

    @GetMapping("/staff/system/alarm")
    @ApiOperation(value = "地图监控员工警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> staffSystemAlarm(@ApiParam("员工id") @NotNull(message = "员工id不能为空") Long userId,LionPage lionPage) {
        return systemAlarmService.list(userId,lionPage);
    }

    @GetMapping("/patient/position")
    @ApiOperation(value = "地图监控患者轨迹(不返回总行数)")
    public IPageResultData<List<Position>> patientPosition(@ApiParam("患者id") @NotNull(message = "患者id不能为空") Long positionId,LionPage lionPage) {
        return positionService.list(positionId, null, lionPage);
    }

    @GetMapping("/patient/system/alarm")
    @ApiOperation(value = "地图监控患者警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> patientSystemAlarm(@ApiParam("患者id") @NotNull(message = "患者id不能为空") Long positionId,LionPage lionPage) {
        return systemAlarmService.list(positionId,lionPage);
    }

    @GetMapping("/temporary/person/position")
    @ApiOperation(value = "地图监控流动人员轨迹(不返回总行数)")
    public IPageResultData<List<Position>> temporaryPersonPosition(@ApiParam("流动人员id") @NotNull(message = "流动人员id不能为空") Long temporaryPersonId,LionPage lionPage) {
        return positionService.list(temporaryPersonId,null , lionPage);
    }

    @GetMapping("/temporary/person/system/alarm")
    @ApiOperation(value = "地图监控流动人员警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> temporaryPersonSystemAlarm(@ApiParam("流动人员id") @NotNull(message = "流动人员id不能为空") Long temporaryPersonId,LionPage lionPage) {
        return systemAlarmService.list(temporaryPersonId,lionPage);
    }

    @GetMapping("/assets/details")
    @ApiOperation(value = "地图监控资产详情")
    public IResultData<AssetsDetailsVo> assetsDetails(@ApiParam("员工id") @NotNull(message = "员工id不能为空") Long assetsId) {
        return ResultData.instance().setData(mapStatisticsService.assetsDetails(assetsId));
    }

    @GetMapping("/alarm/list")
    @ApiOperation(value = "地图监控警告列表")
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(LionPage lionPage){
        return mapStatisticsService.systemAlarmList(lionPage);
    }
}
