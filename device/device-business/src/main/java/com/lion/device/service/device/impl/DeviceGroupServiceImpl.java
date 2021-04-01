package com.lion.device.service.device.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.device.DeviceDao;
import com.lion.device.dao.device.DeviceGroupDao;
import com.lion.device.dao.device.DeviceGroupDeviceDao;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.device.dto.AddDeviceGroupDto;
import com.lion.device.entity.device.dto.UpdateDeviceGroupDto;
import com.lion.device.entity.device.vo.DetailsDeviceGroupVo;
import com.lion.device.entity.device.vo.ListDeviceGroupVo;
import com.lion.device.service.device.DeviceGroupDeviceService;
import com.lion.device.service.device.DeviceGroupService;
import com.lion.device.entity.device.DeviceGroup;
import com.lion.exception.BusinessException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/3/31下午1:48
 */
@Service
public class DeviceGroupServiceImpl extends BaseServiceImpl<DeviceGroup> implements DeviceGroupService {

    @Autowired
    private DeviceGroupDao deviceGroupDao;

    @Autowired
    private DeviceGroupDeviceService deviceGroupDeviceService;

    @Autowired
    private DeviceGroupDeviceDao deviceGroupDeviceDao;

    @Autowired
    private DeviceDao deviceDao;


    @Override
    public void add(AddDeviceGroupDto addDeviceGroupDto) {
        DeviceGroup deviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(addDeviceGroupDto, deviceGroup);
        assertNameExist(deviceGroup.getName(),null);
        assertCodeExist(deviceGroup.getCode(),null);
        deviceGroup = save(deviceGroup);
        deviceGroupDeviceService.add(deviceGroup.getId(),addDeviceGroupDto.getDeviceIds());
    }

    @Override
    public void update(UpdateDeviceGroupDto updateDeviceGroupDto) {
        DeviceGroup deviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(updateDeviceGroupDto, deviceGroup);
        assertNameExist(deviceGroup.getName(),deviceGroup.getId());
        assertCodeExist(deviceGroup.getCode(),deviceGroup.getId());
        update(deviceGroup);
        deviceGroupDeviceService.add(deviceGroup.getId(),updateDeviceGroupDto.getDeviceIds());
    }

    @Override
    public IPageResultData<List<ListDeviceGroupVo>> list(String name, String code, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<DeviceGroup> page = findNavigator(lionPage);
        PageResultData<List<ListDeviceGroupVo>> pageResultData = new PageResultData<List<ListDeviceGroupVo>>(convertListDeviceGroupVo(page.getContent()),page.getPageable(),page.getTotalElements());
        return pageResultData;
    }

    @Override
    public DetailsDeviceGroupVo details(Long id) {
        DeviceGroup deviceGroup = new DeviceGroup();
        if (Objects.nonNull(deviceGroup)) {
            DetailsDeviceGroupVo detailsDeviceGroupVo = new DetailsDeviceGroupVo();
            BeanUtils.copyProperties(deviceGroup, detailsDeviceGroupVo);
            detailsDeviceGroupVo.setDevices(deviceDao.findByDeviceGroupId(id));
            return detailsDeviceGroupVo;
        }
        return null;
    }

    private void assertNameExist(String name, Long id) {
        DeviceGroup deviceGroup = deviceGroupDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(deviceGroup) ){
            BusinessException.throwException("该设备组名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(deviceGroup) && !deviceGroup.getId().equals(id)){
            BusinessException.throwException("该设备组名称已存在");
        }
    }

    private void assertCodeExist(String code, Long id) {
        DeviceGroup deviceGroup = deviceGroupDao.findFirstByCode(code);
        if (Objects.isNull(id) && Objects.nonNull(deviceGroup) ){
            BusinessException.throwException("该设备组编号已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(deviceGroup) && !deviceGroup.getId().equals(id)){
            BusinessException.throwException("该设备组编号已存在");
        }
    }

    private List<ListDeviceGroupVo> convertListDeviceGroupVo(List<DeviceGroup> list){
        List<ListDeviceGroupVo> returnList = new ArrayList<ListDeviceGroupVo>();
        if (Objects.nonNull(list) && list.size()>0){
            list.forEach(deviceGroup -> {
                ListDeviceGroupVo listDeviceGroupVo = new ListDeviceGroupVo();
                BeanUtils.copyProperties(deviceGroup,listDeviceGroupVo);
                listDeviceGroupVo.setDeviceQuantity(deviceGroupDeviceDao.findByDeviceGroupId(deviceGroup.getId()).size());
                listDeviceGroupVo.setDevices(deviceDao.findByDeviceGroupId(deviceGroup.getId()));
                returnList.add(listDeviceGroupVo);
            });
        }
        return returnList;
    }

}
