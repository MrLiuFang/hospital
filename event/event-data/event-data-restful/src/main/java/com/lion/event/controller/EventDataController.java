package com.lion.event.controller;

import cn.hutool.core.util.NumberUtil;
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
import com.lion.event.entity.dto.EventRecordAddDto;
import com.lion.event.entity.dto.OldAlarmToNewAlarm;
import com.lion.event.entity.dto.UnalarmDto;
import com.lion.event.entity.vo.*;
import com.lion.event.service.*;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.event.vo.EventRecordVo;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/5 ??????10:19
 **/
@RestController
@Validated
@Api(tags = {"????????????,????????????,??????????????????"})
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

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;


    @GetMapping("/user/current/region")
    @ApiOperation(value = "??????????????????")
    public IResultData<CurrentRegionVo> userCurrentRegion(@ApiParam(value = "??????id") @NotNull(message = "{3000017}") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.userCurrentRegion(userId));
    }

    @GetMapping("/patient/current/region")
    @ApiOperation(value = "??????????????????")
    public IResultData<CurrentPosition> patientCurrentRegion(@ApiParam(value = "??????id") @NotNull(message = "{3000018}") Long patientId) {
        return ResultData.instance().setData(currentPositionService.find(patientId));
    }

    @GetMapping("/tag/current/region")
    @ApiOperation(value = "??????????????????")
    public IResultData<CurrentPosition> tagCurrentPosition(@ApiParam(value = "??????id") @NotNull(message = "{3000019}") Long tagId) {
        return ResultData.instance().setData(currentPositionService.findByTagId(tagId));
    }

    @GetMapping("/assets/current/region")
    @ApiOperation(value = "??????????????????")
    public IResultData<CurrentPosition> assetsCurrentPosition(@ApiParam(value = "??????id") @NotNull(message = "{3000020}") Long assetsId) {
        return ResultData.instance().setData(currentPositionService.findByAssetsId(assetsId));
    }

    @GetMapping("/wash/list")
    @ApiOperation(value = "??????????????????(???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)")
    public IPageResultData<List<WashRecord>> washList(@ApiParam(value = "??????id") @NotNull(message = "{3000017}") Long userId,
                                                      @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return washService.list(userId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/device/data/list")
    @ApiOperation(value = "??????????????????(??????????????????)")
    public IPageResultData<List<DeviceData>> starList(@ApiParam(value = "starId")  Long starId,@ApiParam(value = "monitorId")  Long monitorId,
                                                      @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                      @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                      LionPage lionPage) {
        return deviceDataService.list(starId, monitorId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/ratio")
    @ApiOperation(value = "????????????????????????/??????????????????")
    public IResultData<ListWashMonitorVo> washRatio(@NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                    @NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) {
        return ResultData.instance().setData(eventService.washRatio(startDateTime,endDateTime));
    }

    @GetMapping("/user/wash/below/standard")
    @ApiOperation(value = "?????????????????????????????????????????????????????????")
    public IPageResultData<List<ListUserWashMonitorVo>> userWashBelowStandard(@ApiParam(value = "????????????") @NotNull(message = "{3000021}") Long userTypeId,
                                                                              @NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                              @NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                              LionPage lionPage) {
        return eventService.userWashRatio(userTypeId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/wash/details")
    @ApiOperation(value = "?????????????????????????????????????????????????????????")
    public IResultData<UserWashDetailsVo> userWashDetails(@ApiParam(value = "??????id") @NotNull(message = "{3000017}") Long userId,
                                                                    @NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                    @NotNull(message = "????????????????????????") @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                    LionPage lionPage) {
        return ResultData.instance().setData(eventService.userWashDetails(userId, startDateTime, endDateTime, lionPage));
    }

    @GetMapping("/user/wash/conformance/ratio")
    @ApiOperation(value = "????????????????????????????????????")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query")
//    })
    public IPageResultData<List<ListUserWashMonitorVo>> userWashConformanceRatio(@ApiParam(value = "????????????") String userName,@ApiParam(value = "??????id")  String departmentIds,@ApiParam(value = "??????id")  String userIds, @ApiParam(value = "????????????")  String userTypeId,
                                                          @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                          @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                          LionPage lionPage) {
        List<Long> listDepartmentIds = new ArrayList<Long>();
        if (StringUtils.hasText(departmentIds)){
            String[] str = departmentIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listDepartmentIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserIds = new ArrayList<Long>();
        if (StringUtils.hasText(userIds)){
            String[] str = userIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserTypeId = new ArrayList<Long>();
        if (StringUtils.hasText(userTypeId)){
            String[] str = userTypeId.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserTypeId.add(Long.valueOf(str[i]));
                }
            }
        }
        return eventService.userWashConformanceRatio(userName,listDepartmentIds,listUserIds , listUserTypeId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/wash/conformance/ratio/screen")
    @ApiOperation(value = "?????????????????????????????????-??????????????????????????????")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userTypeId", value = "????????????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query")
//    })
    public IPageResultData<List<ListWashEventVo1>> userWashConformanceRatioScreen(@ApiParam(value = "????????????") String userName,@ApiParam(value = "??????id")  String departmentIds,@ApiParam(value = "??????id")  String userIds, @ApiParam(value = "????????????")  String userTypeId,
                                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                                 LionPage lionPage) {
        List<Long> listDepartmentIds = new ArrayList<Long>();
        if (StringUtils.hasText(departmentIds)){
            String[] str = departmentIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listDepartmentIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserIds = new ArrayList<Long>();
        if (StringUtils.hasText(userIds)){
            String[] str = userIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserTypeId = new ArrayList<Long>();
        if (StringUtils.hasText(userTypeId)){
            String[] str = userTypeId.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserTypeId.add(Long.valueOf(str[i]));
                }
            }
        }
        return eventService.userWashConformanceRatioScreen(userName,listDepartmentIds ,listUserIds , listUserTypeId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/wash/conformance/ratio/screen/percentage")
    @ApiOperation(value = "?????????????????????????????????-?????????????????????")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userTypeId", value = "????????????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query")
//    })
    public IResultData<Integer> userWashConformanceRatioScreenPercentage(@ApiParam(value = "????????????") String userName, String departmentIds,@ApiParam(value = "??????id")  String userIds, @ApiParam(value = "????????????")  String userTypeId,
                                                                                        @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                        @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) {

        List<Long> listDepartmentIds = new ArrayList<Long>();
        if (StringUtils.hasText(departmentIds)){
            String[] str = departmentIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listDepartmentIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserIds = new ArrayList<Long>();
        if (StringUtils.hasText(userIds)){
            String[] str = userIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserTypeId = new ArrayList<Long>();
        if (StringUtils.hasText(userTypeId)){
            String[] str = userTypeId.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserTypeId.add(Long.valueOf(str[i]));
                }
            }
        }
        return ResultData.instance().setData(eventService.userWashConformanceRatioScreenPercentage(userName,listDepartmentIds ,listUserIds , listUserTypeId, startDateTime, endDateTime));
    }

    @GetMapping("/user/wash/conformance/ratio/export")
    @ApiOperation(value = "??????????????????????????????????????????")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "departmentIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query"),
//            @ApiImplicitParam(name = "userIds", value = "??????id", allowMultiple = true, dataTypeClass = List.class, paramType = "query")
//    })
    public void userWashConformanceRatioExport(@ApiParam(value = "????????????") String userName,String departmentIds,@ApiParam(value = "??????id")  String userIds, @ApiParam(value = "????????????")  String userTypeId,
                                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) throws DocumentException, IOException {
        List<Long> listDepartmentIds = new ArrayList<Long>();
        if (StringUtils.hasText(departmentIds)){
            String[] str = departmentIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listDepartmentIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserIds = new ArrayList<Long>();
        if (StringUtils.hasText(userIds)){
            String[] str = userIds.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserIds.add(Long.valueOf(str[i]));
                }
            }
        }
        List<Long> listUserTypeId = new ArrayList<Long>();
        if (StringUtils.hasText(userTypeId)){
            String[] str = userTypeId.split(",");
            for (int i =0; i<str.length;i++) {
                if (NumberUtil.isLong(str[i])) {
                    listUserTypeId.add(Long.valueOf(str[i]));
                }
            }
        }
        eventService.userWashConformanceRatioExport(userName,listDepartmentIds,listUserIds, listUserTypeId, startDateTime, endDateTime);
    }

    @PutMapping("/unalarm")
    @ApiOperation(value = "????????????(????????????)")
    public IResultData unalarm(@RequestBody UnalarmDto unalarmDto) throws JsonProcessingException {
        systemAlarmService.unalarm(unalarmDto.getId());
        return ResultData.instance();
    }

    @PutMapping("/alarm/report")
    @ApiOperation(value = "????????????")
    public IResultData alarmReport(@RequestBody @Validated AlarmReportDto alarmReportDto) {
        systemAlarmReportService.alarmReport(alarmReportDto);
        return ResultData.instance();
    }

    @PutMapping("/oldAlarm/to/new")
    @ApiOperation(value = "?????????????????????????????????")
    public IResultData oldAlarmToNewAlarm(@RequestBody OldAlarmToNewAlarm oldAlarmToNewAlarm) throws JsonProcessingException {
        systemAlarmService.oldAlarmToNewAlarm(oldAlarmToNewAlarm.getId());
        return ResultData.instance();
    }

    @GetMapping("/region/statistics/details")
    @ApiOperation(value = "??????????????????????????????????????????????????????????????????")
    public IResultData<List<RegionStatisticsDetails>> regionStatisticsDetails(@ApiParam("??????ID") @NotNull(message = "{3000022}") Long buildFloorId){
        return ResultData.instance().setData(mapStatisticsService.regionStatisticsDetails(buildFloorId));
    }

    @GetMapping("/department/statistics/details")
    @ApiOperation(value = "????????????????????????(????????????)")
    public IResultData<DepartmentStatisticsDetailsVo> departmentStatisticsDetails(@ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentStatisticsDetails(departmentId ));
    }

    @GetMapping("/region/statistics/details1")
    @ApiOperation(value = "?????????????????????(????????????)")
    public IResultData<RegionStatisticsDetailsVo> regionStatisticsDetails1(@ApiParam("??????ID") Long regionId) {
        return ResultData.instance().setData(mapStatisticsService.regionStatisticsDetails1(regionId));
    }

    @GetMapping("/department/staff/statistics/details")
    @ApiOperation(value = "??????????????????????????????(????????????)")
    public IResultData<DepartmentStaffStatisticsDetailsVo> departmentStaffStatisticsDetails(@ApiParam(value = "??????????????????-false=??????,true=??????") Boolean isAll,@ApiParam(value = "??????") String name,@ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentStaffStatisticsDetails(isAll, name, regionId, departmentId));
    }

    @GetMapping("/department/assets/statistics/details")
    @ApiOperation(value = "??????????????????????????????(????????????)")
    public IResultData<DepartmentAssetsStatisticsDetailsVo> departmentAssetsStatisticsDetails(@ApiParam(value = "??????/????????????") String keyword,@ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentAssetsStatisticsDetails(keyword, regionId, departmentId));
    }

    @GetMapping("/department/tag/statistics/details")
    @ApiOperation(value = "?????????????????????????????????(????????????)")
    public IResultData<DepartmentTagStatisticsDetailsVo> departmentTagStatisticsDetails(@ApiParam(value = "??????/????????????") String keyword,@ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentTagStatisticsDetails(keyword, regionId, departmentId));
    }

    @GetMapping("/department/patient/statistics/details")
    @ApiOperation(value = "????????????????????????(????????????)")
    public IResultData<DepartmentPatientStatisticsDetailsVo> departmentPatientStatisticsDetails(@ApiParam(value = "??????") String name,@ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentPatientStatisticsDetails(name, regionId, departmentId));
    }

    @GetMapping("/department/temporary/person/statistics/details")
    @ApiOperation(value = "??????????????????????????????(????????????)")
    public IResultData<DepartmentTemporaryPersonStatisticsDetailsVo> departmentTemporaryPersonStatisticsDetails(@ApiParam(value = "??????") String name,@ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId) {
        return ResultData.instance().setData(mapStatisticsService.departmentTemporaryPersonStatisticsDetails(name, regionId, departmentId));
    }

    @GetMapping("/department/device/statistics/details")
    @ApiOperation(value = "???????????????????????????(????????????)")
    public IResultData<DepartmentDeviceStatisticsDetailsVo> departmentDeviceGroupStatisticsDetails(@ApiParam(value = "????????????/??????") String keyword, @ApiParam(value = "??????id") Long regionId, @ApiParam("??????ID") Long departmentId){
        return ResultData.instance().setData(mapStatisticsService.departmentDeviceStatisticsDetails(keyword, regionId, departmentId));
    }

    @GetMapping("/department/region/info")
    @ApiOperation(value = "?????????(????????????)")
    public IResultData<List<DepartmentRegionInfoVo>> regionInfo(@ApiParam(value = "????????????/??????") String keyword,@ApiParam("??????ID") Long departmentId) {
        List<Long> list = departmentExposeService.responsibleDepartment(departmentId);
        List<DepartmentRegionInfoVo> returnList = new ArrayList<>();
        list.forEach(id->{
            com.lion.core.Optional<Department> optional = departmentExposeService.findById(id);
            if (optional.isPresent()) {
                Department department = optional.get();
                DepartmentRegionInfoVo vo = new DepartmentRegionInfoVo();
                vo.setDepartmentId(department.getId());
                vo.setDepartmentName(department.getName());
                vo.setListRegionVos(regionExposeService.find(keyword,id));
                returnList.add(vo);
            }
        });
        return ResultData.instance().setData(returnList);
    }

    @GetMapping("/patient/details")
    @ApiOperation(value = "????????????????????????")
    public IResultData<PatientDetailsVo> patientDetails(@ApiParam("??????id") @NotNull(message = "{3000018}") Long patientId) {
        return ResultData.instance().setData(mapStatisticsService.patientDetails(patientId));
    }

    @GetMapping("/temporary/person/details")
    @ApiOperation(value = "??????????????????????????????")
    public IResultData<TemporaryPersonDetailsVo> TemporaryPersonDetails(@ApiParam("????????????id") @NotNull(message = "{3000023}") Long temporaryPersonId) {
        return ResultData.instance().setData(mapStatisticsService.temporaryPersonDetails(temporaryPersonId));
    }

    @GetMapping("/staff/details")
    @ApiOperation(value = "????????????????????????")
    public IResultData<StaffDetailsVo> staffDetails(@ApiParam("??????id") @NotNull(message = "{3000024}") Long userId) {
        return ResultData.instance().setData(mapStatisticsService.staffDetails(userId));
    }

    @GetMapping("/assets/position")
    @ApiOperation(value = "????????????????????????(??????????????????)")
    public IPageResultData<List<Position>> assetsPosition(@ApiParam("??????id") @NotNull(message = "{3000025}") Long assetsId,@ApiParam("??????id")  Long regionId,
                                                          @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                          @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                          LionPage lionPage) {
        return positionService.list(null,assetsId,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/staff/position")
    @ApiOperation(value = "????????????????????????(??????????????????)")
    public IPageResultData<List<Position>> staffPosition(@ApiParam("??????id") @NotNull(message = "{3000024}") Long userId,@ApiParam("??????id")  Long regionId,
                                                         @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                         @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                         LionPage lionPage) {
        return positionService.list(userId,null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/staff/system/alarm")
    @ApiOperation(value = "??????????????????????????????(??????????????????)")
    public IPageResultData<List<ListSystemAlarmVo>> staffSystemAlarm(@ApiParam("??????id") @NotNull(message = "{3000024}") Long userId,@ApiParam("??????id") Long regionId,
                                                                     @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                     @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(userId,regionId,startDateTime,endDateTime, lionPage);
    }

    @GetMapping("/patient/position")
    @ApiOperation(value = "????????????????????????(??????????????????)")
    public IPageResultData<List<Position>> patientPosition(@ApiParam("??????id") @NotNull(message = "{3000018}") Long patientId,@ApiParam("??????id") Long regionId,
                                                            @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                           @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                           LionPage lionPage) {
        return positionService.list(patientId, null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/patient/system/alarm")
    @ApiOperation(value = "??????????????????????????????(??????????????????)")
    public IPageResultData<List<ListSystemAlarmVo>> patientSystemAlarm(@ApiParam("??????id") @NotNull(message = "{3000018}") Long patientId,@ApiParam("??????id") Long regionId,
                                                                        @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                        @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(patientId, regionId,startDateTime ,endDateTime, lionPage);
    }

    @GetMapping("/temporary/person/position")
    @ApiOperation(value = "??????????????????????????????(??????????????????)")
    public IPageResultData<List<Position>> temporaryPersonPosition(@ApiParam("????????????id") @NotNull(message = "{3000023}") Long temporaryPersonId,@ApiParam("??????id") Long regionId,
                                                                   @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                   @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                   LionPage lionPage) {
        return positionService.list(temporaryPersonId,null,regionId , startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/region/visitor")
    @ApiOperation(value = "??????(??????????????????)")
    public IPageResultData<List<ListVisitorVo>> regionVisitor(@ApiParam("??????") Type type, @ApiParam("??????id") Long regionId,
                                                         @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                         @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                         LionPage lionPage) {
        List<Type> types = new ArrayList<>();
        if (Objects.nonNull(type)) {
            types.add(type);
        }
        return positionService.regionVisitor(types, regionId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/position/export")
    @ApiOperation(value = "????????????")
    public void positionExport(@ApiParam("??????(??????,??????,????????????)id") Long personId, @ApiParam("??????id") Long regionId,
                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, HttpServletResponse response, HttpServletRequest request) throws IOException, IllegalAccessException {
        positionService.positionExport(personId,regionId,startDateTime,endDateTime, response,request );
    }

    @PostMapping("/event/record/add")
    @ApiOperation(value = "??????????????????")
    public IResultData eventRecordAdd(@RequestBody EventRecordAddDto eventRecordAddDto, HttpServletRequest request) throws JsonProcessingException {
        positionService.eventRecordAdd(eventRecordAddDto,request);
        return ResultData.instance();
    }

    @GetMapping("/event/record")
    @ApiOperation(value = "??????????????????")
    public IPageResultData<List<EventRecordVo>> eventRecordList(@ApiParam("????????????")String code, @ApiParam("???????????????")String name,
                                                           @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                           @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                           LionPage lionPage) {
        return positionService.eventRecordList(code, name, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/event/record/export")
    @ApiOperation(value = "????????????????????????")
    public void eventRecordList(@ApiParam("????????????")String code, @ApiParam("???????????????")String name,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) throws IOException, IllegalAccessException {
        positionService.eventRecordListExport(code, name, startDateTime, endDateTime);
    }

    @GetMapping("/event/record/details")
    @ApiOperation(value = "??????????????????")
    public IResultData<EventRecordVo> eventRecordDetails(@ApiParam("id") @NotNull(message = "{0000000}") Long id) {
        return ResultData.instance().setData(positionService.eventRecordDetails(id));
    }

    @GetMapping("/temporary/person/system/alarm")
    @ApiOperation(value = "????????????????????????????????????(??????????????????)")
    public IPageResultData<List<ListSystemAlarmVo>> temporaryPersonSystemAlarm(@ApiParam("????????????id") @NotNull(message = "{3000023}") Long temporaryPersonId,@ApiParam("??????id") Long regionId,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,LionPage lionPage) {
        return systemAlarmService.list(temporaryPersonId,regionId ,startDateTime ,endDateTime, lionPage);
    }

    @GetMapping("/assets/details")
    @ApiOperation(value = "????????????????????????")
    public IResultData<AssetsDetailsVo> assetsDetails(@ApiParam("??????id") @NotNull(message = "{3000024}") Long assetsId) {
        return ResultData.instance().setData(mapStatisticsService.assetsDetails(assetsId));
    }

    @GetMapping("/alarm/list/group")
    @ApiOperation(value = "????????????????????????(??????????????????????????????)??????30????????????")
    public IResultData<List<SystemAlarmGroupVo>> systemAlarmListGroup(@ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                LionPage lionPage){
        return ResultData.instance().setData( mapStatisticsService.systemAlarmGroupList(false, false, null, null,null , null, null, startDateTime, endDateTime, lionPage,"sdt"));
    }

    @GetMapping("/alarm/list")
    @ApiOperation(value = "????????????????????????(??????????????????????????????)??????30????????????")
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList(@ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,@ApiParam(value = "tag id")Long tagId,
                                                                LionPage lionPage){
        return mapStatisticsService.systemAlarmList(false, false, null, null,null , null, null, startDateTime, endDateTime, lionPage, tagId, "sdt");
    }

    @GetMapping("/alarm/list1")
    @ApiOperation(value = "????????????(????????????)??????30????????????")
    public IPageResultData<List<SystemAlarmVo>> systemAlarmList1(@ApiParam("??????id") @RequestParam(value = "ri",required = false) List<Long> ri, @ApiParam("??????id") Long di,@ApiParam("??????false=????????????true=?????????")Boolean alarmState, @ApiParam("????????????") Type alarmType,@ApiParam("????????????") TagType tagType,@ApiParam("?????????") String tagCode,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                LionPage lionPage){
        return mapStatisticsService.systemAlarmList(true, alarmState, ri, di , alarmType, tagType, tagCode, startDateTime, endDateTime, lionPage,null , "dt");
    }

    @GetMapping("/alarm/list1/export")
    @ApiOperation(value = "??????????????????(????????????)??????30????????????")
    public void systemAlarmList1Export(@ApiParam("??????id") @RequestParam(value = "ri",required = false) List<Long> ri, @ApiParam("??????id") Long di, @ApiParam("????????????") Type alarmType,@ApiParam("????????????") TagType tagType,@ApiParam("?????????") String tagCode,
                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                 @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                 LionPage lionPage) throws IOException, DocumentException {
        mapStatisticsService.systemAlarmListExport(true, null, ri, di, alarmType, tagType, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/alarm/details")
    @ApiOperation(value = "????????????????????????")
    public IResultData<SystemAlarmDetailsVo> systemAlarmDetails(@ApiParam("??????id") @NotNull(message = "{3000026}") String id){
        return ResultData.instance().setData(systemAlarmService.details(id));
    }

    @GetMapping("/wash/event/list")
    @ApiOperation(value = "?????????????????????(??????????????????)")
    public IPageResultData<List<ListWashEventVo>> listWashEvent(@ApiParam("????????????")Boolean ia,@ApiParam(value = "????????????id")Long userTypeId, @ApiParam("??????") WashEventType type, @ApiParam("??????")Long regionId,@ApiParam("??????")Long departmentId,@ApiParam("??????")@RequestParam(value="userIds",required = false) List<Long> userIds,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return washEventService.listWashEvent(ia,userTypeId , type, regionId, departmentId, userIds, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/list/export")
    @ApiOperation(value = "?????????????????????PDF??????")
    public void listWashEventExport(@ApiParam("????????????")Boolean ia, @ApiParam("??????") WashEventType type, @ApiParam("??????")Long regionId,@ApiParam("??????")Long departmentId,@ApiParam("??????")@RequestParam(value="userIds",required = false) List<Long> userIds,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        washEventService.listWashEventExport(ia, type, regionId, departmentId, userIds, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/region/ratio")
    @ApiOperation(value = "?????????????????????")
    public IPageResultData<List<ListWashEventRegionVo>> washEventRegionRatio( @ApiParam("??????")Long buildFloorId, @ApiParam("??????")Long regionId,@ApiParam("??????")Long departmentId,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return washEventService.washEventRegionRatio(buildFloorId, regionId, departmentId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/wash/event/region/ratio/export")
    @ApiOperation(value = "???????????????????????????")
    public void washEventRegionRatioExport( @ApiParam("??????")Long buildFloorId, @ApiParam("??????")Long regionId,@ApiParam("??????")Long departmentId,
                                                                              @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                              @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage) throws IOException, DocumentException {
        washEventService.washEventRegionRatioExport(buildFloorId, regionId, departmentId, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/person/all/region")
    @ApiOperation(value = "?????????????????????????????????")
    public IResultData<List<String>> personAllRegion(@ApiParam("?????????????????????id")@NotNull(message = "{3000027}") Long personId,@ApiParam("??????")Long regionId,
                                                     @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                     @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime ) {
        return ResultData.instance().setData(positionService.personAllRegion(personId,regionId , startDateTime, endDateTime));
    }

    @GetMapping("/tag/position")
    @ApiOperation(value = "??????????????????(??????????????????)")
    public IPageResultData<List<ListPositionVo>> tagPosition(@ApiParam("????????????") TagPurpose tagPurpose, @ApiParam("??????")Long regionId, @ApiParam("??????")Long departmentId, @ApiParam("????????????/????????????")String deviceName, @ApiParam("????????????")String tagCode,
                                                       @ApiParam(value = "??????????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                       @ApiParam(value = "??????????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                       LionPage lionPage) {
        return positionService.tagPosition(tagPurpose, regionId, departmentId, deviceName, tagCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/tag/position/export")
    @ApiOperation(value = "????????????????????????")
    public void tagPositionExport(@ApiParam("????????????") TagPurpose tagPurpose, @ApiParam("??????")Long regionId, @ApiParam("??????")Long departmentId, @ApiParam("????????????/????????????")String deviceName, @ApiParam("????????????")String tagCode,
                                                             @ApiParam(value = "??????????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                             @ApiParam(value = "??????????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                             LionPage lionPage) throws IOException, IllegalAccessException {
        positionService.tagPositionExport(tagPurpose, regionId, departmentId, deviceName, tagCode, startDateTime, endDateTime);
    }

    @GetMapping("/tag/temperatureHumidity/list")
    @ApiOperation(value = "????????????(??????????????????)")
    public IPageResultData<List<ListHumitureRecordVo>> temperatureHumidityList(@ApiParam("??????")Long regionId, @ApiParam("??????")Long departmentId, @ApiParam("????????????")String deviceCode,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                               LionPage lionPage){
        return humitureRecordService.temperatureHumidityList(regionId, departmentId, deviceCode, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/tag/temperatureHumidity/list/export")
    @ApiOperation(value = "??????????????????")
    public void temperatureHumidityListExport(@ApiParam("??????")Long regionId, @ApiParam("??????")Long departmentId, @ApiParam("????????????")String deviceCode,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                               @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,
                                                                               LionPage lionPage) throws IOException, IllegalAccessException {
        humitureRecordService.temperatureHumidityListExport(regionId, departmentId, deviceCode, startDateTime, endDateTime);
    }

    @GetMapping("/recyclingBox/history/list")
    @ApiOperation(value = "?????????????????????(??????????????????)")
    public IPageResultData<List<ListRecyclingBoxRecordVo>> recyclingBoxHistoryList(@ApiParam("????????????-true=????????????,false=??????") Boolean isDisinfect, @ApiParam("????????????")TagType tagType,@ApiParam("???????????????")String name,@ApiParam("???????????????")String code,@ApiParam("????????????")String tagCode,
                                                                                  @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                  @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime,@ApiParam("?????????id")Long id,LionPage lionPage){
        return recyclingBoxRecordService.list(isDisinfect, tagType, name, code, tagCode, startDateTime, endDateTime,id , lionPage);
    }

    @GetMapping("/recyclingBox/current/list")
    @ApiOperation(value = "???????????????(??????????????????)")
    public IPageResultData<List<ListRecyclingBoxCurrentVo>> recyclingBoxCurrentList(@ApiParam(value = "????????????????????????(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startPreviousDisinfectDate,
                                                                                    @ApiParam(value = "????????????????????????(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endPreviousDisinfectDate,
                                                                                    @ApiParam(value = "???????????????") String name,@ApiParam(value = "???????????????")String code,LionPage lionPage) {
        return recyclingBoxRecordService.recyclingBoxCurrentList(startPreviousDisinfectDate, endPreviousDisinfectDate, name, code, lionPage);
    }

    @GetMapping("/recyclingBox/current/list/export")
    @ApiOperation(value = "?????????????????????")
    public void recyclingBoxCurrentList(@ApiParam(value = "????????????????????????(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startPreviousDisinfectDate,
                                                                                    @ApiParam(value = "????????????????????????(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endPreviousDisinfectDate,
                                                                                    @ApiParam(value = "???????????????") String name,@ApiParam(value = "???????????????")String code) throws IOException, IllegalAccessException {
        recyclingBoxRecordService.recyclingBoxCurrentListExport(startPreviousDisinfectDate, endPreviousDisinfectDate, name, code);
    }

    @PutMapping("/recyclingBox/disinfect")
    @ApiOperation(value = "?????????????????????")
    public ResultData recyclingBoxDisinfect(@ApiParam(value = "?????????id") Long recyclingBoxId){
        recyclingBoxRecordService.disinfect(recyclingBoxId);
        return ResultData.instance();
    }

    @GetMapping("/user/tag/button/list")
    @ApiOperation(value = "????????????????????????(??????????????????)")
    public IPageResultData<List<ListUserTagButtonRecordVo>> userTagButtonRecordList(@ApiParam(value = "????????????") TagRuleEffect tagRuleEffect,@ApiParam(value = "??????")String name,
                                                                                    @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                    @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return userTagButtonRecordService.list(tagRuleEffect, name, startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/user/tag/button/list/export")
    @ApiOperation(value = "??????????????????????????????")
    public void userTagButtonRecordListExport(@ApiParam(value = "????????????") TagRuleEffect tagRuleEffect,@ApiParam(value = "??????")String name,
                                                                                    @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                                    @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) throws IOException, IllegalAccessException {
        userTagButtonRecordService.export(tagRuleEffect, name, startDateTime, endDateTime);
    }

    @GetMapping("/system/alarm/seven/days/statistics")
    @ApiOperation(value = "???????????????????????????")
    public IResultData<List<SevenDaysStatisticsVo>> sevenDaysStatistics(@ApiParam(value = "??????id") Long departmentId){
        return ResultData.instance().setData(systemAlarmService.sevenDaysStatistics(departmentId));
    }

    @GetMapping("/system/alarm/today/statistics")
    @ApiOperation(value = "??????????????????")
    public IResultData<TodayDaysStatisticsVo> todayDaysStatistics(@ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                  @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime){
        return ResultData.instance().setData(systemAlarmService.todayDaysStatistics(startDateTime, endDateTime));
    }

    @GetMapping("/violation/wash/event")
    @ApiOperation(value = "?????????????????????-(??????????????????)????????????????????????30????????????")
    public IPageResultData<List<ListViolationWashEventVo>> violationWashEvent(@ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                                                   @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime, LionPage lionPage){
        return washEventService.violationWashEvent(startDateTime, endDateTime, lionPage);
    }

    @GetMapping("/count/event")
    @ApiOperation(value = "??????????????????-????????????????????????")
    public IResultData<Integer> count(@ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDateTime,
                                      @ApiParam(value = "????????????(yyyy-MM-dd HH:mm:ss)") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDateTime) {
        return ResultData.instance().setData(washEventService.count(startDateTime,endDateTime));
    }
}
