package com.lion.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;
import com.lion.common.enums.Type;
import com.lion.common.enums.WashEventType;
import com.lion.common.utils.RedisUtil;
import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.enums.TagRuleEffect;
import com.lion.device.entity.enums.TagType;
import com.lion.event.entity.CurrentPosition;
import com.lion.event.entity.DeviceData;
import com.lion.event.entity.Position;
import com.lion.event.entity.WashRecord;
import com.lion.event.entity.dto.AlarmReportDto;
import com.lion.event.entity.dto.OldAlarmToNewAlarm;
import com.lion.event.entity.dto.UnalarmDto;
import com.lion.event.entity.vo.*;
import com.lion.event.service.*;
import com.lion.upms.entity.enums.UserType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午10:19
 **/
@RestController
@Validated
@Api(tags = {"设备数据,地图监控,日志记录……"})
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

    @Autowired
    private WashEventService washEventService;

    @Autowired
    private HumitureRecordService humitureRecordService;

    @Autowired
    private RecyclingBoxRecordService recyclingBoxRecordService;

    @Autowired
    private UserTagButtonRecordService userTagButtonRecordService;

    @Autowired
    private SystemAlarmReportService systemAlarmReportService;

    @GetMapping("/user/current/region")
    @ApiOperation(value = "员工当前位置")
    public IResultData<CurrentRegionVo> userCurrentRegion(@ApiParam(value = "用户id") @NotNull(message = "{3000017}") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.userCurrentRegion(userId));
    }

    @GetMapping("/patient/current/region")
    @ApiOperation(value = "患者当前位置")
    public IResultData<CurrentPosition> patientCurrentRegion(@ApiParam(value = "患者id") @NotNull(message = "{3000018}") Long patientId) {
        return ResultData.instance().setData(currentPositionService.find(patientId));
    }

    @GetMapping("/tag/current/region")
    @ApiOperation(value = "标签当前位置")
    public IResultData<CurrentPosition> tagCurrentPosition(@ApiParam(value = "标签id") @NotNull(message = "{3000019}") Long tagId) {
        return ResultData.instance().setData(currentPositionService.findByTagId(tagId));
    }

    @GetMapping("/assets/current/region")
    @ApiOperation(value = "资产当前位置")
    public IResultData<CurrentPosition> assetsCurrentPosition(@ApiParam(value = "资产id") @NotNull(message = "{3000020}") Long assetsId) {
        return ResultData.instance().setData(currentPositionService.findByAssetsId(assetsId));
    }

    @GetMapping("/wash/list")
    @ApiOperation(value = "用户洗手记录(不返回总行数，数据量大查询总行数费时，不给时间范围默认查询一周内的数据，以提高性能)")
    public IPageResultData<List<WashRecord>> washList(@ApiParam(value = "用户id") @NotNull(message = "{3000017}") Long userId,
                                                      @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return washService.list(userId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/device/data/list")
    @ApiOperation(value = "设备事件记录(不返回总行数)")
    public IPageResultData<List<DeviceData>> starList(@ApiParam(value = "starId")  Long starId,@ApiParam(value = "monitorId")  Long monitorId,
                                                      @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return deviceDataService.list(starId, monitorId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/ratio")
    @ApiOperation(value = "手卫生监控（科室/全院合规率）")
    public IResultData<ListWashMonitorVo> washRatio(@NotNull(message = "开始时间不能为空") @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                    @NotNull(message = "结束时间不能为空") @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) {
        return ResultData.instance().setData(eventService.washRatio(startDateTime,endDateTime));
    }

    @GetMapping("/user/wash/below/standard")
    @ApiOperation(value = "手卫生监控（低于标准人员）不返回总行数")
    public IPageResultData<List<ListUserWashMonitorVo>> userWashBelowStandard(@ApiParam(value = "用户类型") @NotNull(message = "{3000021}") UserType userType,
                                                                              @NotNull(message = "开始时间不能为空") @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                              @NotNull(message = "结束时间不能为空") @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                              LionPage lionPage) {
        return eventService.userWashRatio(userType, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/wash/details")
    @ApiOperation(value = "手卫生监控（用户洗手详细）不返回总行数")
    public IResultData<UserWashDetailsVo> userWashDetails(@ApiParam(value = "用户id") @NotNull(message = "{3000017}") Long userId,
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
        systemAlarmService.unalarm(unalarmDto.getId());
        return ResultData.instance();
    }

    @PutMapping("/alarm/report")
    @ApiOperation(value = "添加汇报")
    public IResultData alarmReport(@RequestBody @Validated AlarmReportDto alarmReportDto) {
        systemAlarmReportService.alarmReport(alarmReportDto);
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
    public IResultData<List<RegionStatisticsDetails>> regionStatisticsDetails(@ApiParam("楼层ID") @NotNull(message = "{3000022}") Long buildFloorId){
        return ResultData.instance().setData(mapStatisticsService.regionStatisticsDetails(buildFloorId));
    }

    @GetMapping("/department/statistics/details")
    @ApiOperation(value = "地图监控科室统计(左边列表)")
    public IResultData<DepartmentStatisticsDetailsVo> departmentStatisticsDetails() {
        return ResultData.instance().setData(mapStatisticsService.departmentStatisticsDetails());
    }

    @GetMapping("/department/staff/statistics/details")
    @ApiOperation(value = "地图监控科室员工统计(左边列表)")
    public IResultData<DepartmentStaffStatisticsDetailsVo> departmentStaffStatisticsDetails(@ApiParam(value = "姓名") String name,@ApiParam(value = "区域id") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.departmentStaffStatisticsDetails(name, regionId));
    }

    @GetMapping("/department/assets/statistics/details")
    @ApiOperation(value = "地图监控科室资产统计(左边列表)")
    public IResultData<DepartmentAssetsStatisticsDetailsVo> departmentAssetsStatisticsDetails(@ApiParam(value = "名称/资产编码") String keyword,@ApiParam(value = "区域id") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.departmentAssetsStatisticsDetails(keyword, regionId));
    }

    @GetMapping("/department/tag/statistics/details")
    @ApiOperation(value = "地图监控科室温标签统计(左边列表)")
    public IResultData<DepartmentTagStatisticsDetailsVo> departmentTagStatisticsDetails(@ApiParam(value = "名称/标签编码") String keyword,@ApiParam(value = "区域id") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.departmentTagStatisticsDetails(keyword, regionId));
    }

    @GetMapping("/department/patient/statistics/details")
    @ApiOperation(value = "地图监控患者统计(左边列表)")
    public IResultData<DepartmentPatientStatisticsDetailsVo> departmentPatientStatisticsDetails(@ApiParam(value = "姓名") String name,@ApiParam(value = "区域id") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.departmentPatientStatisticsDetails(name, regionId));
    }

    @GetMapping("/department/temporary/person/statistics/details")
    @ApiOperation(value = "地图监控流动人员统计(左边列表)")
    public IResultData<DepartmentTemporaryPersonStatisticsDetailsVo> departmentTemporaryPersonStatisticsDetails(@ApiParam(value = "姓名") String name,@ApiParam(value = "区域id") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.departmentTemporaryPersonStatisticsDetails(name, regionId));
    }

    @GetMapping("/department/device/group/statistics/details")
    @ApiOperation(value = "地图监控监控器列表(左边列表)")
    public IResultData<DepartmentDeviceGroupStatisticsDetailsVo> departmentDeviceGroupStatisticsDetails(@ApiParam(value = "设备组名称") String name,@ApiParam(value = "区域id") Long regionId){
        return ResultData.instance().setData(mapStatisticsService.departmentDeviceGroupStatisticsDetails(name, regionId));
    }

    @GetMapping("/patient/details")
    @ApiOperation(value = "地图监控患者详情")
    public IResultData<PatientDetailsVo> patientDetails(@ApiParam("患者id") @NotNull(message = "{3000018}") Long patientId) {
        return ResultData.instance().setData(mapStatisticsService.patientDetails(patientId));
    }

    @GetMapping("/temporary/person/details")
    @ApiOperation(value = "地图监控流动人员详情")
    public IResultData<TemporaryPersonDetailsVo> TemporaryPersonDetails(@ApiParam("流动人员id") @NotNull(message = "{3000023}") Long temporaryPersonId) {
        return ResultData.instance().setData(mapStatisticsService.temporaryPersonDetails(temporaryPersonId));
    }

    @GetMapping("/staff/details")
    @ApiOperation(value = "地图监控员工详情")
    public IResultData<StaffDetailsVo> staffDetails(@ApiParam("员工id") @NotNull(message = "{3000024}") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.staffDetails(userId));
    }

    @GetMapping("/assets/position")
    @ApiOperation(value = "地图监控资产轨迹(不返回总行数)")
    public IPageResultData<List<Position>> assetsPosition(@ApiParam("资产id") @NotNull(message = "{3000025}") Long assetsId,@ApiParam("区域id")  Long regionId,
                                                          @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                          @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                          LionPage lionPage) {
        return positionService.list(null,assetsId,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/staff/position")
    @ApiOperation(value = "地图监控员工轨迹(不返回总行数)")
    public IPageResultData<List<Position>> staffPosition(@ApiParam("员工id") @NotNull(message = "{3000024}") Long userId,@ApiParam("区域id")  Long regionId,
                                                         @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                         @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                         LionPage lionPage) {
        return positionService.list(userId,null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/staff/system/alarm")
    @ApiOperation(value = "地图监控员工警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> staffSystemAlarm(@ApiParam("员工id") @NotNull(message = "{3000024}") Long userId,@ApiParam("区域id") Long regionId,
                                                                     @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                     @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(userId,regionId,startDateTime,endDateTime, lionPage);
    }

    @GetMapping("/patient/position")
    @ApiOperation(value = "地图监控患者轨迹(不返回总行数)")
    public IPageResultData<List<Position>> patientPosition(@ApiParam("患者id") @NotNull(message = "{3000018}") Long patientId,@ApiParam("区域id") Long regionId,
                                                            @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                           @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                           LionPage lionPage) {
        return positionService.list(patientId, null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/patient/system/alarm")
    @ApiOperation(value = "地图监控患者警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> patientSystemAlarm(@ApiParam("患者id") @NotNull(message = "{3000018}") Long patientId,@ApiParam("区域id") Long regionId,
                                                                        @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                        @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(patientId, regionId,startDateTime ,endDateTime, lionPage);
    }

    @GetMapping("/temporary/person/position")
    @ApiOperation(value = "地图监控流动人员轨迹(不返回总行数)")
    public IPageResultData<List<Position>> temporaryPersonPosition(@ApiParam("流动人员id") @NotNull(message = "{3000023}") Long temporaryPersonId,@ApiParam("区域id") Long regionId,
                                                                   @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                   @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                   LionPage lionPage) {
        return positionService.list(temporaryPersonId,null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/temporary/person/system/alarm")
    @ApiOperation(value = "地图监控流动人员警告列表(不返回总行数)")
    public IPageResultData<List<ListSystemAlarmVo>> temporaryPersonSystemAlarm(@ApiParam("流动人员id") @NotNull(message = "{3000023}") Long temporaryPersonId,@ApiParam("区域id") Long regionId,
                                                                               @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                               @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(temporaryPersonId,regionId ,startDateTime ,endDateTime, lionPage);
    }

    @GetMapping("/assets/details")
    @ApiOperation(value = "地图监控资产详情")
    public IResultData<AssetsDetailsVo> assetsDetails(@ApiParam("员工id") @NotNull(message = "{3000024}") Long assetsId) {
        return ResultData.instance().setData(mapStatisticsService.assetsDetails(assetsId));
    }

    @GetMapping("/alarm/list")
    @ApiOperation(value = "地图监控警告列表(只获取负责科室的警告)默认30天内数据")
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(@ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                LionPage lionPage){
        return mapStatisticsService.systemAlarmList(false, false, null, null,null , null, null, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/alarm/list1")
    @ApiOperation(value = "警告记录(查看所有)默认30天内数据")
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList1(@ApiParam("区域id") @RequestParam(value = "ri",required = false) List<Long> ri, @ApiParam("科室id") Long di,@ApiParam("状态false=未处理，true=已处理")Boolean alarmState, @ApiParam("警报来源") Type alarmType,@ApiParam("标签属性") TagType tagType,@ApiParam("标签码") String tagCode,
                                                                @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                LionPage lionPage){
        return mapStatisticsService.systemAlarmList(true, alarmState, ri, di , alarmType, tagType, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/alarm/list1/export")
    @ApiOperation(value = "警告记录导出(查看所有)默认30天内数据")
    public void systemAlarmList1Export(@ApiParam("区域id") @RequestParam(value = "ri",required = false) List<Long> ri, @ApiParam("科室id") Long di, @ApiParam("警报来源") Type alarmType,@ApiParam("标签属性") TagType tagType,@ApiParam("标签码") String tagCode,
                                                                 @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                 @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                 LionPage lionPage) throws IOException, DocumentException {
        mapStatisticsService.systemAlarmListExport(true, null, ri, di, alarmType, tagType, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/alarm/details")
    @ApiOperation(value = "地图监控警告详情")
    public IResultData<SystemAlarmDetailsVo> systemAlarmDetails(@ApiParam("警告id") @NotNull(message = "{3000026}") String id){
        return ResultData.instance().setData(systemAlarmService.details(id));
    }

    @GetMapping("/wash/event/list")
    @ApiOperation(value = "手卫生行为列表(不返回总行数)")
    public IPageResultData<List<ListWashEventVo>> listWashEvent(@ApiParam("是否合规")Boolean ia, @ApiParam("类型") WashEventType type, @ApiParam("区域")Long regionId,@ApiParam("科室")Long departmentId,@ApiParam("员工")@RequestParam(value="userIds",required = false) List<Long> userIds,
                                                                @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return washEventService.listWashEvent(ia, type, regionId, departmentId, userIds, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/list/export")
    @ApiOperation(value = "手卫生行为列表PDF导出")
    public void listWashEventExport(@ApiParam("是否合规")Boolean ia, @ApiParam("类型") WashEventType type, @ApiParam("区域")Long regionId,@ApiParam("科室")Long departmentId,@ApiParam("员工")@RequestParam(value="userIds",required = false) List<Long> userIds,
                                                                @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        washEventService.listWashEventExport(ia, type, regionId, departmentId, userIds, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/region/ratio")
    @ApiOperation(value = "手卫生行为区域")
    public IPageResultData<List<ListWashEventRegionVo>> washEventRegionRatio( @ApiParam("楼层")Long buildFloorId, @ApiParam("区域")Long regionId,@ApiParam("科室")Long departmentId,
                                                                @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return washEventService.washEventRegionRatio(buildFloorId, regionId, departmentId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/region/ratio/export")
    @ApiOperation(value = "手卫生行为区域导出")
    public void washEventRegionRatioExport( @ApiParam("楼层")Long buildFloorId, @ApiParam("区域")Long regionId,@ApiParam("科室")Long departmentId,
                                                                              @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                              @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        washEventService.washEventRegionRatioExport(buildFloorId, regionId, departmentId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/person/all/region")
    @ApiOperation(value = "病人或流动人员所到区域")
    public IResultData<List<String>> personAllRegion(@ApiParam("病人或流动人员id")@NotNull(message = "{3000027}") Long personId,@ApiParam("区域")Long regionId,
                                                     @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                     @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime ) {
        return ResultData.instance().setData(positionService.personAllRegion(personId,regionId , startDateTime, endDateTime));
    }

    @GetMapping("/tag/position")
    @ApiOperation(value = "标签位置列表(不返回总行数)")
    public IPageResultData<List<ListPositionVo>> tagPosition(@ApiParam("标签类型") TagPurpose tagPurpose, @ApiParam("区域")Long regionId, @ApiParam("科室")Long departmentId, @ApiParam("设备名称/标签名称")String deviceName, @ApiParam("标签编码")String tagCode,
                                                       @ApiParam(value = "开始进入时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                       @ApiParam(value = "结束进入时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                       LionPage lionPage) {
        return positionService.tagPosition(tagPurpose, regionId, departmentId, deviceName, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/tag/temperatureHumidity/list")
    @ApiOperation(value = "温湿记录(不返回总行数)")
    public IPageResultData<List<ListHumitureRecordVo>> temperatureHumidityList(@ApiParam("区域")Long regionId, @ApiParam("科室")Long departmentId, @ApiParam("设备编码")String deviceCode,
                                                                               @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                               @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                               LionPage lionPage){
        return humitureRecordService.temperatureHumidityList(regionId, departmentId, deviceCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/recyclingBox/history/list")
    @ApiOperation(value = "回收箱历史记录(不返回总行数)")
    public IPageResultData<List<ListRecyclingBoxRecordVo>> recyclingBoxHistoryList(@ApiParam("是否消毒-true=历史记录,false=当前") Boolean isDisinfect, @ApiParam("标签类型")TagType tagType,@ApiParam("回收箱名称")String name,@ApiParam("回收箱编码")String code,@ApiParam("标签编码")String tagCode,
                                                                                  @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                  @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage){
        return recyclingBoxRecordService.list(isDisinfect, tagType, name, code, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/recyclingBox/current/list")
    @ApiOperation(value = "回收箱当前(不返回总行数)")
    public IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentList(@ApiParam(value = "上次开始消毒时间(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startPreviousDisinfectDate,
                                                                                    @ApiParam(value = "上次结束消毒时间(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endPreviousDisinfectDate,
                                                                                    @ApiParam(value = "回收箱名称") String name,@ApiParam(value = "回收箱编号")String code,LionPage lionPage) {
        return recyclingBoxRecordService.recyclingBoxCurrentList(startPreviousDisinfectDate, endPreviousDisinfectDate, name, code, lionPage);
    }

    @PutMapping("/recyclingBox/disinfect")
    @ApiOperation(value = "回收箱一键消毒")
    public ResultData recyclingBoxDisinfect(@ApiParam(value = "回收箱id") Long recyclingBoxId){
        recyclingBoxRecordService.disinfect(recyclingBoxId);
        return ResultData.instance();
    }

    @GetMapping("/user/tag/button/list")
    @ApiOperation(value = "员工标签按钮日志(不返回总行数)")
    public IPageResultData<List<ListUserTagButtonRecordVo>> userTagButtonRecordList(@ApiParam(value = "操作类型") TagRuleEffect tagRuleEffect,@ApiParam(value = "姓名")String name,
                                                                                    @ApiParam(value = "开始时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                    @ApiParam(value = "结束时间(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return userTagButtonRecordService.list(tagRuleEffect, name, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/system/alarm/seven/days/statistics")
    @ApiOperation(value = "近七日警报数量统计")
    public IResultData<List<SevenDaysStatisticsVo>> sevenDaysStatistics(@ApiParam(value = "科室id") Long departmentId){
        return ResultData.instance().setData(systemAlarmService.sevenDaysStatistics(departmentId));
    }

    @GetMapping("/system/alarm/today/statistics")
    @ApiOperation(value = "今日警报统计")
    public IResultData<TodayDaysStatisticsVo> todayDaysStatistics(){
        return ResultData.instance().setData(systemAlarmService.todayDaysStatistics());
    }

}
