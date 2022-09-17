package com.lion.manage.service.assets.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.curd.MoreEntity;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsBorrowDao;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.dto.AddAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.ReturnAssetsBorrowDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsBorrowVo;
import com.lion.manage.entity.assets.vo.ListAssetsBorrowVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.State;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.assets.AssetsTypeService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import com.lion.manage.utils.ExcelColumn;
import com.lion.manage.utils.ExportExcelUtil;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:16
 */
@Service
public class AssetsBorrowServiceImpl extends BaseServiceImpl<AssetsBorrow> implements AssetsBorrowService {

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private AssetsBorrowDao assetsBorrowDao;

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private WardRoomSickbedService wardRoomSickbedService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private RegionService regionService;

    @DubboReference
    private FileExposeService fileExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private WardRoomService wardRoomService;

    @Autowired
    private WardService wardService;

    @DubboReference
    private RoleExposeService roleExposeService;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Autowired
    private HttpServletResponse response;

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Autowired
    private AssetsTypeService assetsTypeService;

    @Override
    @Transactional
    public void add(AddAssetsBorrowDto addAssetsBorrowDto) {
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            AssetsBorrow assetsBorrow = assetsBorrowDao.findFirstByAssetsIdAndReturnUserIdIsNull(assetsId);
            if (Objects.nonNull(assetsBorrow)) {
                com.lion.core.Optional<Assets> optional = assetsService.findById(assetsId);
                BusinessException.throwException((optional.isEmpty()?"":optional.get().getName())+ MessageI18nUtil.getMessage("2000061"));
            }
        });
        User user = userExposeService.find(addAssetsBorrowDto.getBorrowUserNumber());
        if (Objects.isNull(user)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("2000062"));
        }
        com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(addAssetsBorrowDto.getBorrowDepartmentId());
        if (optionalDepartment.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("2000063"));
        }
        Department department = optionalDepartment.get();
        com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedService.findById(addAssetsBorrowDto.getBorrowWardRoomSickbedId());
        if (optionalWardRoomSickbed.isEmpty()) {
            BusinessException.throwException(MessageI18nUtil.getMessage("2000064"));
        }
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            assertAssetsExist(assetsId);
        });
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            AssetsBorrow assetsBorrow = new AssetsBorrow();
            assetsBorrow.setAssetsId(assetsId);
            assetsBorrow.setBorrowDepartmentId(department.getId());
            if (optionalWardRoomSickbed.isPresent()) {
                assetsBorrow.setBorrowWardRoomSickbedId(optionalWardRoomSickbed.get().getId());
            }
            assetsBorrow.setBorrowUserId(user.getId());
            assetsBorrow.setStartDateTime(addAssetsBorrowDto.getStartDateTime());
            assetsBorrow.setEndDateTime(addAssetsBorrowDto.getEndDateTime());
            save(assetsBorrow);
            com.lion.core.Optional<Assets> optional  = assetsService.findById(assetsBorrow.getAssetsId());
            if (optional.isPresent()) {
                Assets assets = optional.get();
                assets.setDeviceState(State.USED);
                assetsService.update(assets);
            }
        });
    }

    @Override
    public IPageResultData<List<ListAssetsBorrowVo>> list(String name, Long borrowUserId, Long assetsTypeId, Long departmentId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage) {
        List<Long> departmentIds = departmentService.responsibleDepartment(departmentId);
        Page page = assetsDao.list(name, borrowUserId,departmentIds , assetsTypeId, assetsId, startDateTime, endDateTime, isReturn, lionPage);
        List<MoreEntity> list = page.getContent();
        List<ListAssetsBorrowVo> returnList = new ArrayList<>();
        list.forEach(moreEntity -> {
            Assets assets = (Assets) moreEntity.getEntity1();
            AssetsBorrow assetsBorrow = (AssetsBorrow) moreEntity.getEntity2();
            ListAssetsBorrowVo vo = new ListAssetsBorrowVo();
            BeanUtils.copyProperties(assets,vo);
            com.lion.core.Optional<Build> optionalBuild = buildService.findById(assets.getBuildId());
            vo.setBuildName(optionalBuild.isEmpty()?"":optionalBuild.get().getName());
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(assets.getBuildFloorId());
            vo.setBuildFloorName(optionalBuildFloor.isEmpty()?"":optionalBuildFloor.get().getName());
            com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(assets.getDepartmentId());
            vo.setDepartmentName(optionalDepartment.isEmpty()?"":optionalDepartment.get().getName());
            com.lion.core.Optional<Region> optionalRegion = regionService.findById(assets.getRegionId());
            vo.setRegionName(optionalRegion.isEmpty()?"":optionalRegion.get().getName());
            TagAssets tagAssets = tagAssetsExposeService.find(assets.getId());
            if (Objects.nonNull(tagAssets)){
                com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagAssets.getTagId());
                if (optionalTag.isPresent()){
                    vo.setTagCode(optionalTag.get().getTagCode());
                }
            }


            com.lion.core.Optional<Department> optionalDepartment1 = departmentService.findById(assetsBorrow.getBorrowDepartmentId());
            vo.setBorrowDepartmentId(assetsBorrow.getBorrowDepartmentId());
            vo.setBorrowDepartmentName(optionalDepartment1.isEmpty()?"":optionalDepartment1.get().getName());
            com.lion.core.Optional<WardRoomSickbed> optionalWardRoomSickbed = wardRoomSickbedService.findById(assetsBorrow.getBorrowWardRoomSickbedId());
            vo.setBorrowWardRoomSickbedId(assetsBorrow.getBorrowWardRoomSickbedId());
            vo.setBorrowWardRoomSickbedCode(optionalWardRoomSickbed.isEmpty()?"":optionalWardRoomSickbed.get().getBedCode());
            vo.setRegistrationTime(assetsBorrow.getCreateDateTime());
            vo.setStartDateTime(assetsBorrow.getStartDateTime());
            vo.setEndDateTime(assetsBorrow.getEndDateTime());

//            if (Objects.nonNull(wardRoomSickbed)){
//                WardRoom wardRoom = wardRoomService.findById(wardRoomSickbed.getWardRoomId());
//                if (Objects.nonNull(wardRoom)) {
//                    Ward ward = wardService.findById(wardRoom.getWardId());
//                    if (Objects.nonNull(ward)) {
//
//                    }
//                }
//            }

            com.lion.core.Optional<User> optionalUser = userExposeService.findById(assetsBorrow.getBorrowUserId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                vo.setBorrowUserName(user.getName());
                vo.setBorrowUserHeadPortrait(user.getHeadPortrait());
                vo.setBorrowUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            com.lion.core.Optional<User> optionalUser1 = userExposeService.findById(assetsBorrow.getReturnUserId());
            if (optionalUser1.isPresent()){
                User user = optionalUser.get();
                vo.setReturnUserName(user.getName());
                vo.setReturnUserHeadPortrait(user.getHeadPortrait());
                vo.setReturnUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            vo.setReturnTime(assetsBorrow.getReturnTime());
            vo.setAssetsBorrowId(assetsBorrow.getId());
            com.lion.core.Optional<AssetsType> assetsTypeOptional = assetsTypeService.findById(assets.getAssetsTypeId());
            vo.setAssetsType(assetsTypeOptional.isPresent()?assetsTypeOptional.get():null);
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    public void export(String name, Long borrowUserId, Long assetsTypeId, Long departmentId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListAssetsBorrowVo>> pageResultData = list(name,borrowUserId,assetsTypeId,departmentId,assetsId,startDateTime,endDateTime,isReturn,lionPage);
        List<ListAssetsBorrowVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("assets name", "name"));
        excelColumn.add(ExcelColumn.build("borrow user name", "borrowUserName"));
        excelColumn.add(ExcelColumn.build("department name", "borrowDepartmentName"));
        excelColumn.add(ExcelColumn.build("use datetime", "startDateTime"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("assetsBorrow.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    @Transactional
    public void returnAssetsBorrow(ReturnAssetsBorrowDto returnAssetsBorrowDto) {

        User user = userExposeService.find(returnAssetsBorrowDto.getNumber());
        if (Objects.isNull(user)) {
            BusinessException.throwException(MessageI18nUtil.getMessage("2000065"));
        }

        if (Objects.nonNull(returnAssetsBorrowDto.getAssetsBorrowIds()) && returnAssetsBorrowDto.getAssetsBorrowIds().size()>0) {
            returnAssetsBorrowDto.getAssetsBorrowIds().forEach(id->{
                com.lion.core.Optional<AssetsBorrow> optional = findById(id);
                if (optional.isPresent()) {
                    AssetsBorrow assetsBorrow = optional.get();
                    assetsBorrow.setReturnUserId(user.getId());
                    assetsBorrow.setReturnTime(LocalDateTime.now());
                    update(assetsBorrow);
                    if (Objects.nonNull(assetsBorrow)) {
                        com.lion.core.Optional<Assets> optionalAssets = assetsService.findById(assetsBorrow.getAssetsId());
                        if (optionalAssets.isPresent()) {
                            Assets assets = optionalAssets.get();
                            assets.setDeviceState(State.NOT_USED);
                            assetsService.update(assets);
                        }
                    }
                }
            });
        }
    }

    @Override
    public DetailsAssetsBorrowVo lastDetails(Long assetsId) {
        AssetsBorrow assetsBorrow = this.assetsBorrowDao.findFirstByAssetsIdOrderByCreateDateTimeDesc(assetsId);
        if (Objects.isNull(assetsBorrow)) {
            return null;
        }
        DetailsAssetsBorrowVo vo = new DetailsAssetsBorrowVo();
        BeanUtils.copyProperties(assetsBorrow,vo);
        com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(vo.getBorrowDepartmentId());
        if (optionalDepartment.isPresent()) {
            vo.setBorrowDepartmentName(optionalDepartment.get().getName());
        }
        com.lion.core.Optional<User> optionalBorrowUser = userExposeService.findById(vo.getBorrowUserId());
        if (optionalBorrowUser.isPresent()) {
            User user = optionalBorrowUser.get();
            vo.setBorrowUserName(user.getName());
            vo.setBorrowUserHeadPortrait(user.getHeadPortrait());
            vo.setBorrowUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
        }
        com.lion.core.Optional<User> optionalReturnUser = userExposeService.findById(vo.getBorrowUserId());
        if (optionalReturnUser.isPresent()) {
            User user = optionalReturnUser.get();
            vo.setReturnUserName(user.getName());
            vo.setReturnUserHeadPort(user.getHeadPortrait());
            vo.setReturnUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
        }
        return vo;
    }

    @Override
    public AssetsBorrow findFirstByAssetsIdAndReturnUserIdIsNull(Long assetsId) {
        return assetsBorrowDao.findFirstByAssetsIdAndReturnUserIdIsNull(assetsId);
    }

    private void assertAssetsExist(Long id) {
        com.lion.core.Optional<Assets> optional = this.assetsService.findById(id);
        if (!optional.isPresent() ){
            BusinessException.throwException(optional.get().getCode()+MessageI18nUtil.getMessage("2000066"));
        }
    }

}
