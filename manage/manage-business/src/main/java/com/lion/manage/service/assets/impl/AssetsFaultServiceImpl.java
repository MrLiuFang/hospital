package com.lion.manage.service.assets.impl;

import com.lion.common.expose.file.FileExposeService;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.core.persistence.JpqlParameter;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsFaultDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.AddAssetsFaultDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsFaultDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsFaultVo;
import com.lion.manage.entity.assets.vo.ListAssetsFaultVo;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsFaultService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.manage.service.build.BuildFloorService;
import com.lion.manage.service.build.BuildService;
import com.lion.manage.service.department.DepartmentService;
import com.lion.manage.service.region.RegionService;
import com.lion.manage.utils.ExcelColumn;
import com.lion.manage.utils.ExportExcelUtil;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import com.lion.utils.MessageI18nUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.lion.core.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:19
 */
@Service
public class AssetsFaultServiceImpl extends BaseServiceImpl<AssetsFault> implements AssetsFaultService {

    @Autowired
    private AssetsFaultDao assetsFaultDao;

    @DubboReference
    private UserExposeService userExposeService;

    @Autowired
    private AssetsService assetsService;

    @DubboReference
    private FileExposeService fileExposeService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private BuildFloorService buildFloorService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private AssetsBorrowService assetsBorrowService;

    @Autowired
    private AssetsFaultService assetsFaultService;


    @Override
    @Transactional
    public void add(AddAssetsFaultDto addAssetsFaultDto) {
        AssetsFault assetsFault = new AssetsFault();
        BeanUtils.copyProperties(addAssetsFaultDto,assetsFault);
        assertUserExist(assetsFault.getDeclarantUserId());
        assertAssetsExist(assetsFault.getAssetsId());
        assetsFault.setDeclarantTime(LocalDateTime.now());
        if (Objects.equals(assetsFault.getState(),AssetsFaultState.FINISH)){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000067"));
        }
        int i = assetsFaultDao.countByAssetsIdAndState(assetsFault.getAssetsId(),AssetsFaultState.NOT_FINISHED);
        if (i>0) {
            BusinessException.throwException("有未處理的故障,不能再繼續申報故障");
        }
        save(assetsFault);
        Optional<Assets> optional =  assetsService.findById(assetsFault.getAssetsId());
        if (optional.isPresent()) {
            Assets assets = optional.get();
            assets.setIsFault(true);
            assetsService.update(assets);
        }
    }

    @Override
    public void update(UpdateAssetsFaultDto updateAssetsFaultDto) {
        AssetsFault assetsFault = new AssetsFault();
        BeanUtils.copyProperties(updateAssetsFaultDto,assetsFault);
//        assertUserExist(assetsFault.getDeclarantUserId());
//        assertAssetsExist(assetsFault.getAssetsId());
        if (Objects.equals( assetsFault.getState(), AssetsFaultState.FINISH)) {
            assetsFault.setFinishTime(LocalDateTime.now());
            Optional<Assets> optional =  assetsService.findById(assetsFault.getAssetsId());
            if (optional.isPresent()) {
                Assets assets = optional.get();
                assets.setIsFault(false);
                assetsService.update(assets);
            }
        }
        super.update(assetsFault);
    }

    @Override
    public IPageResultData<List<ListAssetsFaultVo>> list(Long departmentId, AssetsFaultState state, Long assetsId, String code, String assetsCode, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) {
        JpqlParameter jpqlParameter = new JpqlParameter();
        List<Long> departmentIds = departmentService.responsibleDepartment(departmentId);
        List<Long> ids = new ArrayList<>();
        if (departmentIds.size()>0) {
            List<Assets> list = assetsService.findByDepartmentId(departmentIds);
            ids.add(Long.MAX_VALUE);
            for (Assets assets : list) {
                ids.add(assets.getId());
            }
        }
        if (StringUtils.hasText(keyword)) {
            List<AssetsFault> assetsFaults = assetsFaultDao.findByCodeLikeOrDescribeLike("%"+keyword+"%","%"+keyword+"%");
            List<Long> _ids = new ArrayList<>();
            List<Long> __ids = new ArrayList<>();
            assetsFaults.forEach(assetsFault -> {
                _ids.add(assetsFault.getAssetsId());
                __ids.add(assetsFault.getId());
            });
            ids = ids.stream().filter(item -> _ids.contains(item)).collect(toList());
            if (ids.size()<=0){
                ids.add(Long.MAX_VALUE);
            }
            if (__ids.size()>0) {
                jpqlParameter.setSearchParameter(SearchConstant.IN+"_id",__ids);
            }
        }
        if (Objects.nonNull(assetsId)) {
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_assetsId",assetsId);
        }
        if (Objects.nonNull(state)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_state",state);
        }
        if (StringUtils.hasText(code)){
            jpqlParameter.setSearchParameter(SearchConstant.EQUAL+"_code",code);
        }
        if (StringUtils.hasText(assetsCode)) {
            List<Assets> list = assetsService.find(assetsCode);
            List<Long> _ids = new ArrayList<>();
            list.forEach(assets -> {
                _ids.add(assets.getId());
            });
            ids = ids.stream().filter(item -> _ids.contains(item)).collect(toList());
            if (ids.size()<=0){
                ids.add(Long.MAX_VALUE);
            }
        }
        if (ids.size()>0){
            jpqlParameter.setSearchParameter(SearchConstant.IN+"_assetsId",ids.stream().distinct().collect(toList()));
        }
        jpqlParameter.setSortParameter("createDateTime", Sort.Direction.DESC);
        lionPage.setJpqlParameter(jpqlParameter);
        Page<AssetsFault> page = findNavigator(lionPage);
        List<AssetsFault> list = page.getContent();
        List<ListAssetsFaultVo> listAssetsFaultVos = new ArrayList<ListAssetsFaultVo>();
        list.forEach(assetsFault -> {
            ListAssetsFaultVo vo = new ListAssetsFaultVo();
            BeanUtils.copyProperties(assetsFault,vo);
            if (Objects.nonNull(assetsFault.getDeclarantUserId())) {
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(assetsFault.getDeclarantUserId());
                if (optionalUser.isPresent()){
                    User user = optionalUser.get();
                    vo.setDeclarantUserName(user.getName());
                    vo.setDeclarantUserHeadPortrait(user.getHeadPortrait());
                    vo.setDeclarantUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            com.lion.core.Optional<Assets> optionalAssets = assetsService.findById(assetsFault.getAssetsId());
            if (optionalAssets.isPresent()){
                Assets assets = optionalAssets.get();
                vo.setDeviceCode(assets.getCode());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setName(assets.getName());
                com.lion.core.Optional<Build> optionalBuild = buildService.findById(assets.getBuildId());
                if (optionalBuild.isPresent()) {
                    vo.setBuildName(optionalBuild.get().getName());
                }
                com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (optionalBuildFloor.isPresent()){
                    vo.setBuildFloorName(optionalBuildFloor.get().getName());
                }
                com.lion.core.Optional<Region> optionalRegion = regionService.findById(assets.getRegionId());
                if (optionalRegion.isPresent()){
                    vo.setRegionName(optionalRegion.get().getName());
                }
                com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(assets.getDepartmentId());
                if (optionalDepartment.isPresent()){
                    vo.setDepartmentName(optionalDepartment.get().getName());
                }
            }
            listAssetsFaultVos.add(vo);
        });
        return new PageResultData(listAssetsFaultVos, page.getPageable(), page.getTotalElements());
    }

    @Override
    public void export(Long departmentId, AssetsFaultState state, Long assetsId, String code, String assetsCode, String keyword, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListAssetsFaultVo>> pageResultData = list(departmentId,state,assetsId,code,assetsCode,keyword,startDateTime,endDateTime,lionPage);
        List<ListAssetsFaultVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("編碼", "code"));
        excelColumn.add(ExcelColumn.build("名稱", "name"));
        excelColumn.add(ExcelColumn.build("設備編碼", "deviceCode"));
        excelColumn.add(ExcelColumn.build("科室名稱", "departmentName"));
        excelColumn.add(ExcelColumn.build("區域名稱", "regionName"));
        excelColumn.add(ExcelColumn.build("故障描述", "describe"));
        excelColumn.add(ExcelColumn.build("創建時間", "createDateTime"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("assetsFault.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public DetailsAssetsFaultVo details(Long id) {
        com.lion.core.Optional<AssetsFault> optionalAssetsFault = this.findById(id);
        if (optionalAssetsFault.isPresent()) {
            AssetsFault assetsFault = optionalAssetsFault.get();
            DetailsAssetsFaultVo vo = new DetailsAssetsFaultVo();
            BeanUtils.copyProperties(assetsFault, vo);
            if (Objects.nonNull(assetsFault.getDeclarantUserId())) {
                com.lion.core.Optional<User> optionalUser = userExposeService.findById(assetsFault.getDeclarantUserId());
                if (optionalUser.isPresent()){
                    User user = optionalUser.get();
                    vo.setDeclarantUserName(user.getName());
                    vo.setDeclarantUserHeadPortrait(user.getHeadPortrait());
                    vo.setDeclarantUserHeadPortraitUrl(fileExposeService.getUrl(user.getHeadPortrait()));
                }
            }
            com.lion.core.Optional<Assets> optionalAssets = assetsService.findById(assetsFault.getAssetsId());
            if (optionalAssets.isPresent()){
                Assets assets = optionalAssets.get();
                vo.setDeviceCode(assets.getCode());
                vo.setImg(assets.getImg());
                vo.setImgUrl(fileExposeService.getUrl(assets.getImg()));
                vo.setName(assets.getName());
                com.lion.core.Optional<Build> optionalBuild = buildService.findById(assets.getBuildId());
                if (optionalBuild.isPresent()) {
                    vo.setBuildName(optionalBuild.get().getName());
                }
                com.lion.core.Optional<BuildFloor> optionalBuildFloor = buildFloorService.findById(assets.getBuildFloorId());
                if (optionalBuildFloor.isPresent()){
                    vo.setBuildFloorName(optionalBuildFloor.get().getName());
                }
                com.lion.core.Optional<Region> optionalRegion = regionService.findById(assets.getRegionId());
                if (optionalRegion.isPresent()){
                    vo.setRegionName(optionalRegion.get().getName());
                }
                com.lion.core.Optional<Department> optionalDepartment = departmentService.findById(assets.getDepartmentId());
                if (optionalDepartment.isPresent()){
                    vo.setDepartmentName(optionalDepartment.get().getName());
                }
            }
            return vo;
        }
        return null;
    }

    @Override
    public List<AssetsFault> find(String keyword) {
        return assetsFaultDao.findByCodeLikeOrDescribeLike("%"+keyword+"%","%"+keyword+"%");
    }

    private void assertAssetsExist(Long id) {
        com.lion.core.Optional<Assets> optional = this.assetsService.findById(id);
        if (!optional.isPresent()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000066"));
        }
    }


    private void assertUserExist(Long id) {
        com.lion.core.Optional<User> optional = this.userExposeService.findById(id);
        if (optional.isEmpty()){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000068"));
        }
    }
}
