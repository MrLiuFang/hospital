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
import com.lion.manage.entity.assets.dto.AddAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.ReturnAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.ListAssetsBorrowVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsType;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.region.Region;
import com.lion.manage.entity.ward.WardRoomSickbed;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentResponsibleUserExposeService;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.service.ward.WardRoomService;
import com.lion.manage.service.ward.WardRoomSickbedService;
import com.lion.manage.service.ward.WardService;
import com.lion.upms.entity.role.Role;
import com.lion.upms.entity.role.RoleUser;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.role.RoleExposeService;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.CurrentUserUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    @DubboReference
    private DepartmentResponsibleUserExposeService departmentResponsibleUserExposeService;

    @Override
    @Transactional
    public void add(AddAssetsBorrowDto addAssetsBorrowDto) {
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            AssetsBorrow assetsBorrow = assetsBorrowDao.findFirstByAssetsIdAndReturnUserIdIsNull(assetsId);
            if (Objects.nonNull(assetsBorrow)) {
                Assets assets = assetsService.findById(assetsId);
                BusinessException.throwException((Objects.isNull(assets)?"":assets.getName())+"已借用,未归还");
            }
        });
        User user = userExposeService.find(addAssetsBorrowDto.getBorrowUserNumber());
        if (Objects.isNull(user)) {
            BusinessException.throwException("借用人不存在");
        }
        Department department = departmentService.findById(addAssetsBorrowDto.getBorrowDepartmentId());
        if (Objects.isNull(department)) {
            BusinessException.throwException("借用科室不存在");
        }
        WardRoomSickbed wardRoomSickbed = wardRoomSickbedService.findById(addAssetsBorrowDto.getBorrowWardRoomSickbedId());
        if (Objects.isNull(wardRoomSickbed)) {
            BusinessException.throwException("借用床位不存在");
        }
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            assertAssetsExist(assetsId);
        });
        addAssetsBorrowDto.getAssetsIds().forEach(assetsId->{
            AssetsBorrow assetsBorrow = new AssetsBorrow();
            assetsBorrow.setAssetsId(assetsId);
            assetsBorrow.setBorrowDepartmentId(department.getId());
            assetsBorrow.setBorrowWardRoomSickbedId(wardRoomSickbed.getId());
            assetsBorrow.setBorrowUserId(user.getId());
            assetsBorrow.setStartDateTime(addAssetsBorrowDto.getStartDateTime());
            assetsBorrow.setEndDateTime(addAssetsBorrowDto.getEndDateTime());
            save(assetsBorrow);

            Assets assets = assetsService.findById(assetsBorrow.getAssetsId());
            if (Objects.nonNull(assets)) {
                assets.setUseState(AssetsUseState.USEING);
                assetsService.update(assets);
            }
        });
    }

    @Override
    public IPageResultData<List<ListAssetsBorrowVo>> list(String name, Long borrowUserId, AssetsType type, Long departmentId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage) {
        List<Long> departmentIds = departmentExposeService.responsibleDepartment(departmentId);
        Page page = assetsDao.list(name, borrowUserId,departmentIds , type, assetsId, startDateTime, endDateTime, isReturn, lionPage);
        List<MoreEntity> list = page.getContent();
        List<ListAssetsBorrowVo> returnList = new ArrayList<>();
        list.forEach(moreEntity -> {
            Assets assets = (Assets) moreEntity.getEntity1();
            AssetsBorrow assetsBorrow = (AssetsBorrow) moreEntity.getEntity2();
            ListAssetsBorrowVo vo = new ListAssetsBorrowVo();
            BeanUtils.copyProperties(assets,vo);
            Build build = buildService.findById(assets.getBuildId());
            vo.setBuildName(Objects.isNull(build)?"":build.getName());
            BuildFloor buildFloor = buildFloorService.findById(assets.getBuildFloorId());
            vo.setBuildFloorName(Objects.isNull(buildFloor)?"":buildFloor.getName());
            Department department = departmentService.findById(assets.getDepartmentId());
            vo.setDepartmentName(Objects.isNull(department)?"":department.getName());
            Region region = regionService.findById(assets.getRegionId());
            vo.setRegionName(Objects.isNull(region)?"":region.getName());
            TagAssets tagAssets = tagAssetsExposeService.find(assets.getId());
            if (Objects.nonNull(tagAssets)){
                Tag tag = tagExposeService.findById(tagAssets.getTagId());
                if (Objects.nonNull(tag)){
                    vo.setTagCode(tag.getTagCode());
                }
            }


            department = departmentService.findById(assetsBorrow.getBorrowDepartmentId());
            vo.setBorrowDepartmentId(assetsBorrow.getBorrowDepartmentId());
            vo.setBorrowDepartmentName(Objects.isNull(department)?"":department.getName());
            WardRoomSickbed wardRoomSickbed = wardRoomSickbedService.findById(assetsBorrow.getBorrowWardRoomSickbedId());
            vo.setBorrowWardRoomSickbedId(assetsBorrow.getBorrowWardRoomSickbedId());
            vo.setBorrowWardRoomSickbedCode(Objects.isNull(wardRoomSickbed)?"":wardRoomSickbed.getBedCode());
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

            User user = userExposeService.findById(assetsBorrow.getBorrowUserId());
            if (Objects.nonNull(user)){
                vo.setBorrowUserName(user.getName());
                vo.setBorrowUserHeadPortrait(user.getHeadPortrait());
                vo.setBorrowUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            user = userExposeService.findById(assetsBorrow.getReturnUserId());
            if (Objects.nonNull(user)){
                vo.setReturnUserName(user.getName());
                vo.setReturnUserHeadPortrait(user.getHeadPortrait());
                vo.setReturnUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
            }
            vo.setReturnTime(assetsBorrow.getReturnTime());
            vo.setAssetsBorrowId(assetsBorrow.getId());
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,page.getPageable(),page.getTotalElements());
    }

    @Override
    @Transactional
    public void returnAssetsBorrow(ReturnAssetsBorrowDto returnAssetsBorrowDto) {

        User user = userExposeService.find(returnAssetsBorrowDto.getNumber());
        if (Objects.isNull(user)) {
            BusinessException.throwException("归还人编号不存在");
        }

        if (Objects.nonNull(returnAssetsBorrowDto.getAssetsBorrowIds()) && returnAssetsBorrowDto.getAssetsBorrowIds().size()>0) {
            returnAssetsBorrowDto.getAssetsBorrowIds().forEach(id->{
                AssetsBorrow assetsBorrow = new AssetsBorrow();
                assetsBorrow.setId(id);
                assetsBorrow.setReturnUserId(user.getId());
                assetsBorrow.setReturnTime(LocalDateTime.now());
                update(assetsBorrow);

                assetsBorrow = findById(id);
                if (Objects.nonNull(assetsBorrow)) {
                    Assets assets = assetsService.findById(assetsBorrow.getAssetsId());
                    if (Objects.nonNull(assets)) {
                        assets.setUseState(AssetsUseState.NOT_USED);
                        assetsService.update(assets);
                    }
                }
            });
        }
    }

    private void assertAssetsExist(Long id) {
        Assets assets = this.assetsService.findById(id);
        if (Objects.isNull(assets) ){
            BusinessException.throwException(assets.getCode()+"该资产不存在");
        }
    }

}
