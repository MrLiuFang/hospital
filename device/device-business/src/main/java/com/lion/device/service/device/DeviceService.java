package com.lion.device.service.device;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.vo.DeviceStatisticsVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:46
 */
public interface DeviceService extends BaseService<Device> {

    /**
     * 删除设备
     * @param deleteDtoList
     */
   public void delete(List<DeleteDto> deleteDtoList);

    /**
     * 设备统计
     * @return
     */
   public DeviceStatisticsVo statistics();
}
