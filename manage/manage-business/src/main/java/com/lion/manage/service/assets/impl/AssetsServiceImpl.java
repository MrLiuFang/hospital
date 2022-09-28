package com.lion.manage.service.assets.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.Optional;
import com.lion.core.PageResultData;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.entity.tag.Tag;
import com.lion.device.entity.tag.TagAssets;
import com.lion.device.expose.tag.TagAssetsExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.expose.service.CurrentPositionExposeService;
import com.lion.event.expose.service.SystemAlarmExposeService;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsBorrowDao;
import com.lion.manage.dao.assets.AssetsDao;
import com.lion.manage.dao.assets.AssetsFaultDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.dto.AddAssetsDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsVo;
import com.lion.manage.entity.assets.vo.ListAssetsVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.enums.AssetsUseState;
import com.lion.manage.entity.enums.State;
import com.lion.manage.entity.enums.SystemAlarmType;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.department.DepartmentExposeService;
import com.lion.manage.expose.department.DepartmentUserExposeService;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsFaultService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.assets.AssetsTypeService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.utils.ExcelColumn;
import com.lion.manage.utils.ExportExcelUtil;
import com.lion.utils.CurrentUserUtil;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:15
 */
@Service
public class AssetsServiceImpl extends BaseServiceImpl<Assets> implements AssetsService {

    @Autowired
    private AssetsDao assetsDao;

    @Autowired
    private AssetsBorrowDao assetsBorrowDao;

    @Autowired
    private AssetsBorrowService assetsBorrowService;

    @Autowired
    private AssetsFaultDao assetsFaultDao;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RegionService regionService;

    @DubboReference
    private TagExposeService tagExposeService;

    @DubboReference
    private TagAssetsExposeService tagAssetsExposeService;

    @DubboReference
    private CurrentPositionExposeService currentPositionExposeService;

    @DubboReference
    private SystemAlarmExposeService systemAlarmExposeService;

    @Autowired
    private AssetsFaultService assetsFaultService;

    @DubboReference
    private DepartmentUserExposeService departmentUserExposeService;

    @Autowired
    private AssetsTypeService assetsTypeService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HttpServletResponse response;

    @DubboReference
    private DepartmentExposeService departmentExposeService;

    @Override
    @Transactional
    //    @GlobalTransactional
    public void add(AddAssetsDto addAssetsDto) {
        Assets assets = new Assets();
        BeanUtils.copyProperties(addAssetsDto,assets);
        assertNameExist(assets.getName(),null);
        assertCodeExist(assets.getCode(),null);
        assertRegionExist(assets.getRegionId());
        assets = setBuildAndFloorAndDepartment(assets);
        assertBuildExist(assets.getBuildId());
        assertBuildFloorExist(assets.getBuildFloorId());
        assertDepartmentExist(assets.getDepartmentId());
        assets = this.save(assets);
        if (Objects.nonNull(addAssetsDto.getTagCode())) {
            tagAssetsExposeService.relation(assets.getId(), addAssetsDto.getTagCode(), assets.getDepartmentId());
        }
        persistenceRedis(assets,addAssetsDto.getTagCode());
    }

    @Override
    @Transactional
    //    @GlobalTransactional
    public void update(UpdateAssetsDto updateAssetsDto) {
        Assets assets = new Assets();
        BeanUtils.copyProperties(updateAssetsDto,assets);
        assertNameExist(assets.getName(),assets.getId());
        assertCodeExist(assets.getCode(),assets.getId());
        assertRegionExist(assets.getRegionId());
        assets = setBuildAndFloorAndDepartment(assets);
        assertBuildExist(assets.getBuildId());
        assertBuildFloorExist(assets.getBuildFloorId());
        assertDepartmentExist(assets.getDepartmentId());
        this.update(assets);
        Tag tag = tagExposeService.find(assets.getId());
        if (Objects.nonNull(tag)) {
            redisTemplate.delete(RedisConstants.TAG_ASSETS + tag.getId());
        }
        if (StringUtils.hasText(updateAssetsDto.getTagCode())) {
            tagAssetsExposeService.relation(assets.getId(), updateAssetsDto.getTagCode(), assets.getDepartmentId());
            persistenceRedis(assets,updateAssetsDto.getTagCode());
        }else {
            tagAssetsExposeService.deleteByAssetsId(assets.getId());
        }
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            Tag tag = tagExposeService.find(deleteDto.getId());
            if (Objects.nonNull(tag)) {
                redisTemplate.delete(RedisConstants.TAG_ASSETS + tag.getId());
            }
            redisTemplate.delete(RedisConstants.ASSETS+deleteDto.getId());
            this.deleteById(deleteDto.getId());
            assetsBorrowDao.deleteByAssetsId(deleteDto.getId());
            assetsFaultDao.deleteByAssetsId(deleteDto.getId());
            tagAssetsExposeService.deleteByAssetsId(deleteDto.getId());
            currentPositionExposeService.delete(null,deleteDto.getId(),null);
        });
    }

    @Override
    public DetailsAssetsVo details(Long id) {
        com.lion.core.Optional<Assets> optionalAssets = this.findById(id);
        if (optionalAssets.isEmpty()) {
            return null;
        }
        Assets assets = optionalAssets.get();
        DetailsAssetsVo detailsAssetsVo = new DetailsAssetsVo();
        BeanUtils.copyProperties(assets,detailsAssetsVo);
        detailsAssetsVo.setBorrowCount(assetsBorrowDao.countByAssetsId(assets.getId()));
        detailsAssetsVo.setFaultCount(assetsFaultDao.countByAssetsId(assets.getId()));
        detailsAssetsVo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
        if (Objects.nonNull(assets.getBuildId())){
            com.lion.core.Optional<Build> optionalBuild = buildService.findById(assets.getBuildId());
            if (optionalBuild.isPresent()){
                Build build = optionalBuild.get();
                detailsAssetsVo.setPosition(build.getName());
                detailsAssetsVo.setBuildName(build.getName());
            }
        }
        if (Objects.nonNull(assets.getBuildFloorId())){
            com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(assets.getBuildFloorId());
            if (optionalBuildFloor.isPresent()){
                BuildFloor buildFloor = optionalBuildFloor.get();
                detailsAssetsVo.setPosition(detailsAssetsVo.getPosition()+buildFloor.getName());
                detailsAssetsVo.setBuildFloorName(buildFloor.getName());
            }
        }
        if (Objects.nonNull(assets.getDepartmentId())){
            com.lion.core.Optional<Department> optional = departmentService.findById(assets.getDepartmentId());
            if (optional.isPresent()){
                detailsAssetsVo.setDepartmentName(optional.get().getName());
            }
        }
        if (Objects.nonNull(assets.getRegionId())) {
            com.lion.core.Optional<Region> optional = regionService.findById(assets.getRegionId());
            if (optional.isPresent()){
                detailsAssetsVo.setRegionName(optional.get().getName());
            }
        }
        TagAssets tagAssets = tagAssetsExposeService.find(assets.getId());
        if (Objects.nonNull(tagAssets)) {
            com.lion.core.Optional<Tag> optional = tagExposeService.findById(tagAssets.getTagId());
            if (optional.isPresent()) {
                Tag tag = optional.get();
                detailsAssetsVo.setTagCode(tag.getTagCode());
                detailsAssetsVo.setTagId(tag.getId());
            }
        }
        AssetsFault assetsFault = assetsFaultDao.findFirstByAssetsIdAndStateOrderByCreateDateTimeDesc(assets.getId(), AssetsFaultState.NOT_FINISHED);
        if (Objects.nonNull(assetsFault)) {
            detailsAssetsVo.setAssetsFault(assetsFaultService.details(assetsFault.getId()));
        }
        SystemAlarm systemAlarm =  systemAlarmExposeService.findLastByAssetsId(assets.getId());
        if (Objects.nonNull(systemAlarm)) {
            SystemAlarmType systemAlarmType = SystemAlarmType.instance(systemAlarm.getSat());
            detailsAssetsVo.setAlarm(systemAlarmType.getDesc());
            detailsAssetsVo.setAlarmType(systemAlarmType);
            detailsAssetsVo.setAlarmDataTime(systemAlarm.getDt());
            detailsAssetsVo.setAlarmId(systemAlarm.get_id());
        }
        detailsAssetsVo.setDetailsAssetsBorrowVo(assetsBorrowService.lastDetails(assets.getId()));
        Optional<AssetsType> assetsTypeOptional = assetsTypeService.findById(assets.getAssetsTypeId());
        if (assetsTypeOptional.isPresent()) {
            detailsAssetsVo.setAssetsTypeName(assetsTypeOptional.get().getAssetsTypeName());
        }
        return detailsAssetsVo;
    }

    @Override
    public Assets findByTagId(Long tagId) {
        TagAssets tagAssets =  tagAssetsExposeService.findByTagId(tagId);
        if (Objects.nonNull(tagAssets)) {
            com.lion.core.Optional<Assets> optional = findById(tagAssets.getAssetsId());
            return optional.isPresent()?optional.get():null;
        }
        return null;
    }

    @Override
    public List<Assets> findByDepartmentId(List<Long> departmentIds) {
        return assetsDao.findByDepartmentIdIn(departmentIds);
    }

    @Override
    public List<Assets> find(String code) {
        return assetsDao.findByCodeLike("%"+code+"%");
    }

    @Override
    public List<Assets> findByKeyword(String keyword) {
        return assetsDao.findByCodeLikeOrNameLike("%"+keyword+"%","%"+keyword+"%");
    }

    @Override
    public IPageResultData<List<ListAssetsVo>> list(Boolean isBorrowed, String name, String code, Long departmentId, Boolean isMyDepartment, Long assetsTypeId, AssetsUseState useState, String tagCode, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        if (Objects.equals(isBorrowed,true)) {
            List<AssetsBorrow> list = assetsBorrowDao.findFirstByReturnUserIdIsNull();
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            list.forEach(assetsBorrow -> {
                ids.add(assetsBorrow.getAssetsId());
            });
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",ids);
        }else if (Objects.equals(isBorrowed,false)) {
            List<AssetsBorrow> list = assetsBorrowDao.findFirstByReturnUserIdIsNull();
            List<Long> ids = new ArrayList<>();
            ids.add(Long.MAX_VALUE);
            list.forEach(assetsBorrow -> {
                ids.add(assetsBorrow.getAssetsId());
            });
            jpqlParameter.setSearchParameter(SearchConstant.NOT_IN+"_id",ids);
        }
        if (StringUtils.hasText(name)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_name",name);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.LIKE+"_code",code);
        }
        if (Objects.nonNull(assetsTypeId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsTypeId",assetsTypeId);
        }
        if (Objects.nonNull(useState)) {
            if (Objects.equals(useState,AssetsUseState.USEING)) {
                jpqlParameter.setSearchParameter(SearchConstant.EQUAL + "_deviceState", State.USED);
            }else if (Objects.equals(useState,AssetsUseState.NOT_USED)) {
                jpqlParameter.setSearchParameter(SearchConstant.EQUAL + "_deviceState", State.NOT_USED);
            }

        }
        if (StringUtils.hasText(tagCode)) {
            List<TagAssets> tagAssets =  tagAssetsExposeService.findByTagCode(tagCode);
            if (Objects.nonNull(tagAssets) && tagAssets.size()>0) {
                List<Long> ids = new ArrayList<>();
                tagAssets.forEach(tagAssets1 -> {
                    ids.add(tagAssets1.getAssetsId());
                });
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",ids);
            }else {
                jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_id",Long.MAX_VALUE);
            }
        }
        if (Objects.equals(isMyDepartment,true)) {
            Department department = departmentUserExposeService.findDepartment(CurrentUserUtil.getCurrentUserId());
            List<Long> ids = departmentExposeService.responsibleDepartment(null);
            if (Objects.nonNull(ids) && ids.size()>0){
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_departmentId",ids);
            }
        }
        if (Objects.nonNull(departmentId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_departmentId",departmentId);
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<Assets> page = findNavigator(lionPage);
        List<Assets> list = page.getContent();
        List<ListAssetsVo> listAssetsVos = new ArrayList<ListAssetsVo>();
        list.forEach(assets -> {
            ListAssetsVo listAssetsVo = new ListAssetsVo();
            BeanUtils.copyProperties(assets,listAssetsVo);
            if (Objects.nonNull(assets.getBuildId())){
                com.lion.core.Optional<Build> optionalBuild = buildService.findById(assets.getBuildId());
                if (optionalBuild.isPresent()){
                    listAssetsVo.setPosition(optionalBuild.get().getName());
                }
            }
            if (Objects.nonNull(assets.getBuildFloorId())){
                com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (optionalBuildFloor.isPresent()){
                    listAssetsVo.setPosition(listAssetsVo.getPosition()+optionalBuildFloor.get().getName());
                }
            }
            if (Objects.nonNull(assets.getDepartmentId())){
                com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(assets.getDepartmentId());
                if (optionalDepartment.isPresent()){
                    Department department = optionalDepartment.get();
                    listAssetsVo.setDepartmentName(department.getName());
                    TagAssets tagAssets = tagAssetsExposeService.find(assets.getId());
                    if (Objects.nonNull(tagAssets)) {
                        com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(tagAssets.getTagId());
                        if (optionalTag.isPresent()) {
                            listAssetsVo.setTagCode(optionalTag.get().getTagCode());
                        }
                    }
                }
            }
            com.lion.core.Optional<AssetsType> optionalAssetsType = assetsTypeService.findById(assets.getAssetsTypeId());
            listAssetsVo.setAssetsType(optionalAssetsType.isPresent()?optionalAssetsType.get():null);
            listAssetsVos.add(listAssetsVo);
        });
        return new PageResultData(listAssetsVos, page.getPageable(), page.getTotalElements());
    }

    @Override
    public void export(String name, String code, Long departmentId, Boolean isMyDepartment, Long assetsTypeId, AssetsUseState useState, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListAssetsVo>> pageResultData = list(null, name, code, departmentId, isMyDepartment, assetsTypeId, useState,null , lionPage);
        List<ListAssetsVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("name", "name"));
        excelColumn.add(ExcelColumn.build("type", "assetsType.assetsTypeName"));
        excelColumn.add(ExcelColumn.build("code", "code"));
        excelColumn.add(ExcelColumn.build("departmentName", "departmentName"));
        excelColumn.add(ExcelColumn.build("position", "position"));
        excelColumn.add(ExcelColumn.build("useRegistration", "useRegistration"));
        excelColumn.add(ExcelColumn.build("useState", "useState"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("assets.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    private void assertDepartmentExist(Long id) {
        com.lion.core.Optional<Department> optional = this.departmentService.findById(id);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000069"));
        }
    }
    private void assertBuildExist(Long id) {
        com.lion.core.Optional<Build> optional = this.buildService.findById(id);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000070"));
        }
    }
    private void assertBuildFloorExist(Long id) {
        com.lion.core.Optional<BuildFloor> optional = this.buildFloorService.findById(id);
        if (optional.isEmpty() ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000071"));
        }
    }
    private void assertRegionExist(Long id) {
        com.lion.core.Optional<Region> optional = this.regionService.findById(id);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000072"));
        }
    }

    private void assertNameExist(String name, Long id) {
        Assets assets = assetsDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(assets)) || (Objects.nonNull(id) && Objects.nonNull(assets) && !Objects.equals(assets.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000073"));
        }
    }

    private void assertCodeExist(String code, Long id) {
        Assets assets = assetsDao.findFirstByCode(code);
        if ((Objects.isNull(id) && Objects.nonNull(assets)) || (Objects.nonNull(id) && Objects.nonNull(assets) && !Objects.equals(assets.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000074"));
        }
    }

    private Assets setBuildAndFloorAndDepartment(Assets assets){
        com.lion.core.Optional<Region> optional = regionService.findById(assets.getRegionId());
        if (optional.isEmpty()) {
            return assets;
        }
        Region region = optional.get();
        assets.setBuildId(region.getBuildId());
        assets.setBuildFloorId(region.getBuildFloorId());
        assets.setDepartmentId(region.getDepartmentId());
        return assets;
    }

    private void persistenceRedis(Assets assets,String tagCode){
        Tag tag = null;
        if (Objects.nonNull(tagCode)){
            tag = tagExposeService.find(tagCode);
        }
        redisTemplate.opsForValue().set(RedisConstants.ASSETS+assets.getId(),assets,5, TimeUnit.MINUTES);
        if (Objects.nonNull(tag)) {
            redisTemplate.opsForValue().set(RedisConstants.TAG_ASSETS + tag.getId(), assets.getId(), 5, TimeUnit.MINUTES);
        }

    }
}
