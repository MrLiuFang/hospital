package com.lion.device.service.device;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.device.entity.device.dto.AddDeviceGroupDto;
import com.lion.device.entity.device.dto.UpdateDeviceGroupDto;
import com.lion.device.entity.device.vo.DetailsDeviceGroupVo;
import com.lion.device.entity.device.vo.ListDeviceGroupVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:47
 */
public interface DeviceGroupService extends BaseService<DeviceGroup> {

    /**
     * 新增设备组
     * @param addDeviceGroupDto
     */
    public void add(AddDeviceGroupDto addDeviceGroupDto);

    /**
     * 更新设备组
     * @param updateDeviceGroupDto
     */
    public void update(UpdateDeviceGroupDto updateDeviceGroupDto);

    /**
     * 列表
     * @param name
     * @param code
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListDeviceGroupVo>> list( String name, String code, LionPage lionPage );

    /**
     * 设备组详情
     * @param id
     * @return
     */
    public DetailsDeviceGroupVo details(Long id);

    /**
     * 删除设备组
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
