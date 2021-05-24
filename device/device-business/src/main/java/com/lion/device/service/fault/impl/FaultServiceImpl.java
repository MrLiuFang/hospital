package com.lion.device.service.fault.impl;

import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.fault.FaultDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.device.DeviceGroupDevice;
import com.lion.device.entity.enums.FaultType;
import com.lion.device.entity.fault.Fault;
import com.lion.device.entity.fault.dto.AddFaultDto;
import com.lion.device.entity.fault.dto.UpdateFaultDto;
import com.lion.device.entity.fault.vo.FaultDetailsVo;
import com.lion.device.entity.fault.vo.ListFaultVo;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.service.fault.FaultService;
import com.lion.exception.BusinessException;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.assets.AssetsExposeService;
import com.lion.manage.expose.build.BuildExposeService;
import com.lion.manage.expose.build.BuildFloorExposeService;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.region.RegionExposeService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/24 上午8:47
 */
@Service
public class FaultServiceImpl extends BaseServiceImpl<Fault> implements FaultService {

    @Autowired
    private FaultDao faultDao;

    @DubboReference
    private AssetsExposeService assetsExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private BuildExposeService buildExposeService;

    @DubboReference
    private BuildFloorExposeService buildFloorExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @DubboReference
    private UserExposeService userExposeService;

    @Override
    public void save(AddFaultDto addFaultDto) {
        Fault fault = new Fault();
        BeanUtils.copyProperties(addFaultDto,fault);
        setInfo(fault);
        assertUserNumberExist(fault.getUserNumber());
        super.save(fault);
    }

    @Override
    public void update(UpdateFaultDto updateFaultDto) {
        Fault fault = new Fault();
        BeanUtils.copyProperties(updateFaultDto,fault);
        setInfo(fault);
        assertUserNumberExist(fault.getUserNumber());
        super.update(fault);
    }

    @Override
    public FaultDetailsVo details(Long id) {
        Fault fault = this.findById(id);
        if (Objects.nonNull(fault)) {
            FaultDetailsVo vo = new FaultDetailsVo();
            BeanUtils.copyProperties(fault,vo);
            vo = setInfoVo(vo);
            return vo;
        }
        return null;
    }

    @Override
    public IPageResultData<List<ListFaultVo>> list(FaultType type, String code, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.nonNull(type)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_type",type);
        }
        if (StringUtils.hasText(code)) {
            jpqlParameter.setSearchParameter(SearchConstant.LIKE + "_code", code);
        }
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Fault> page = this.findNavigator(lionPage);
        List<Fault> list = page.getContent();
        List<ListFaultVo> returnList = new ArrayList<>();
        list.forEach(fault -> {
            ListFaultVo vo = new ListFaultVo();
            BeanUtils.copyProperties(fault,vo);
            vo = (ListFaultVo) setInfoVo(vo);
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    public FaultDetailsVo setInfoVo(FaultDetailsVo vo) {
        Region region = regionExposeService.findById(vo.getRegionId());
        if (Objects.nonNull(region)) {
            vo.setRegionName(region.getName());
        }
        Build build = buildExposeService.findById(vo.getBuildId());
        if (Objects.nonNull(build)){
            vo.setBuildName(build.getName());
        }
        BuildFloor buildFloor = buildFloorExposeService.findById(vo.getBuildFloorId());
        if (Objects.nonNull(buildFloor)){
            vo.setBuildFloorName(buildFloor.getName());
        }
        Department department = departmentExposeService.findById(vo.getDepartmentId());
        if (Objects.nonNull(department)) {
            vo.setDepartmentName(department.getName());
        }
        return vo;
    }

    private Fault setInfo(Fault entity) {
        Long regionId = null;
        if (Objects.equals(entity.getType(), FaultType.ASSETS)) {
            Assets assets = assetsExposeService.find(entity.getCode());
            if (Objects.nonNull(assets)) {
                regionId = assets.getRegionId();
                entity.setRelationId(assets.getId());
            }else {
                BusinessException.throwException("该资产编码不存在");
            }
        }else if (Objects.equals(entity.getType(), FaultType.ASSETS)) {
            Cctv cctv = cctvExposeService.find(entity.getCode());
            if (Objects.nonNull(cctv)) {
                regionId = cctv.getRegionId();
                entity.setRelationId(cctv.getId());
            }else {
                BusinessException.throwException("该CCTV编码不存在");
            }
        }
        Region region = regionExposeService.findById(regionId);
        entity.setRegionId(region.getId());
        if (Objects.nonNull(region)) {
            if (Objects.nonNull(regionId)) {
                Build build = buildExposeService.findById(region.getBuildId());
                if (Objects.nonNull(build)){
                    entity.setBuildId(build.getId());
                }
                BuildFloor buildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
                if (Objects.nonNull(buildFloor)){
                    entity.setBuildFloorId(buildFloor.getId());
                }
                Department department = departmentExposeService.findById(region.getDepartmentId());
                if (Objects.nonNull(department)) {
                    entity.setDepartmentId(department.getId());
                }
            }
        }
        return entity;
    }

    private void assertUserNumberExist(Integer userNumber) {
        User user = userExposeService.find(userNumber);
        if (Objects.isNull(user)){
            BusinessException.throwException("该员工编号不存在");
        }
    }
}
