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
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;

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
        com.lion.core.Optional<Fault> optional = this.findById(id);
        if (optional.isPresent()) {
            Fault fault = optional.get();
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
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
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

    @Override
    public int countNotSolve() {
        return faultDao.countByIsSolveAndType(false,FaultType.DEVICE);
    }

    public FaultDetailsVo setInfoVo(FaultDetailsVo vo) {
        com.lion.core.Optional<Region> optionalRegion = regionExposeService.findById(vo.getRegionId());
        if (optionalRegion.isPresent()) {
            vo.setRegionName(optionalRegion.get().getName());
        }
        com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(vo.getBuildId());
        if (optionalBuild.isPresent()){
            vo.setBuildName(optionalBuild.get().getName());
        }
        com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(vo.getBuildFloorId());
        if (optionalBuildFloor.isPresent()){
            vo.setBuildFloorName(optionalBuildFloor.get().getName());
        }
        com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(vo.getDepartmentId());
        if (optionalDepartment.isPresent()) {
            vo.setDepartmentName(optionalDepartment.get().getName());
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
                BusinessException.throwException(MessageI18nUtil.getMessage("4000040"));
            }
        }else if (Objects.equals(entity.getType(), FaultType.ASSETS)) {
            Cctv cctv = cctvExposeService.find(entity.getCode());
            if (Objects.nonNull(cctv)) {
                regionId = cctv.getRegionId();
                entity.setRelationId(cctv.getId());
            }else {
                BusinessException.throwException(MessageI18nUtil.getMessage("4000041"));
            }
        }
        com.lion.core.Optional<Region> optional = regionExposeService.findById(regionId);
        if (optional.isPresent()){
            Region region = optional.get();
            entity.setRegionId(region.getId());
            com.lion.core.Optional<Build> optionalBuild = buildExposeService.findById(region.getBuildId());
            if (optionalBuild.isPresent()){
                entity.setBuildId(optionalBuild.get().getId());
            }
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorExposeService.findById(region.getBuildFloorId());
            if (optionalBuildFloor.isPresent()){
                entity.setBuildFloorId(optionalBuildFloor.get().getId());
            }
            com.lion.core.Optional<Department> optionalDepartment = departmentExposeService.findById(region.getDepartmentId());
            if (optionalDepartment.isPresent()) {
                entity.setDepartmentId(optionalDepartment.get().getId());
            }
        }

        return entity;
    }

    private void assertUserNumberExist(Integer userNumber) {
        User user = userExposeService.find(userNumber);
        if (Objects.isNull(user)){
            BusinessException.throwException(MessageI18nUtil.getMessage("1000033"));
        }
    }
}
