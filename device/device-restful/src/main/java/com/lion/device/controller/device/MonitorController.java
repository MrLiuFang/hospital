package com.lion.device.controller.device;

import com.lion.core.IPageResultData;
import com.lion.core.IResultData;
import com.lion.core.LionPage;
import com.lion.core.ResultData;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DeviceMonitorTopVo;
import com.lion.device.entity.device.vo.ListDeviceMonitorVo;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
import com.lion.device.service.cctv.CctvService;
import com.lion.device.service.device.DeviceService;
import com.lion.device.service.fault.FaultService;
import com.lion.device.service.tag.TagService;
import com.lion.manage.expose.assets.AssetsExposeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/27 上午9:27
 */
@RestController
@RequestMapping("/device/monitor")
@Validated
@Api(tags = {"设备监控"})
public class MonitorController {

    @Autowired
    private DeviceService deviceService;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CctvService cctvService;

    @Autowired
    private FaultService faultService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/top")
    @ApiOperation(value = "设备监控顶部")
    public IResultData<DeviceMonitorTopVo> top(){
        DeviceMonitorTopVo deviceMonitorTopVo = new DeviceMonitorTopVo();
        List<Long> ids = deviceService.allId();
        deviceMonitorTopVo.setNormalCount(ids.size());
        deviceMonitorTopVo = calculation(ids, deviceMonitorTopVo);
//        deviceMonitorTopVo = calculation(assetsExposeService.allId(), deviceMonitorTopVo);
//        deviceMonitorTopVo = calculation(tagService.allId(), deviceMonitorTopVo);
//        deviceMonitorTopVo = calculation(cctvService.allId(), deviceMonitorTopVo);
        int fault = faultService.countNotSolve();
        deviceMonitorTopVo.setNormalCount(deviceMonitorTopVo.getNormalCount()-fault);
        deviceMonitorTopVo.setFaultCount(fault);
        return ResultData.instance().setData(deviceMonitorTopVo);
    }

    @GetMapping("/list")
    @ApiOperation(value = "设备监控列表")
    public IPageResultData<List<ListDeviceMonitorVo>> list(@ApiParam(value = "建筑ID") Long buildId, @ApiParam(value = "建筑楼层ID") Long buildFloorId, @ApiParam(value = "设备大类") DeviceClassify deviceClassify,@ApiParam(value = "设备小类") DeviceType deviceType, @ApiParam(value = "状态") State state, @ApiParam(value = "名称")String name, LionPage lionPage){
        return deviceService.deviceMonitorList(buildId, buildFloorId,deviceClassify ,deviceType , state, name, lionPage);
    }

    private DeviceMonitorTopVo calculation(List<Long> ids, DeviceMonitorTopVo vo){
        ids.forEach(id->{
            com.lion.core.Optional<Device> optional = deviceService.findById(id);
            if (optional.isPresent()) {
                Device device = optional.get();
                if (Objects.nonNull(device.getLastDataTime())) {
                    Duration duration = Duration.between(device.getLastDataTime(), LocalDateTime.now());
                    if (duration.toMinutes() > 120) {
                        vo.setOfflineCount(vo.getOfflineCount() + 1);
                        vo.setNormalCount(vo.getNormalCount() - 1);
                    }
                } else {
                    vo.setOfflineCount(vo.getOfflineCount() + 1);
                    vo.setNormalCount(vo.getNormalCount() - 1);
                }
            }
        });
        return vo;
    }
}
