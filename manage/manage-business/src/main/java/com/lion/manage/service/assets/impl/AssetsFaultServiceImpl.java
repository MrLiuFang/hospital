package com.lion.manage.service.assets.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsFaultDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.AddAssetsFaultDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsFaultDto;
import com.lion.manage.entity.enums.AssetsFaultState;
import com.lion.manage.service.assets.AssetsFaultService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public void add(AddAssetsFaultDto addAssetsFaultDto) {
        AssetsFault assetsFault = new AssetsFault();
        BeanUtils.copyProperties(addAssetsFaultDto,assetsFault);
        assertUserExist(assetsFault.getDeclarantUserId());
        assertAssetsExist(assetsFault.getAssetsId());
        assetsFault.setDeclarantTime(LocalDateTime.now());
        if (Objects.equals(assetsFault.getState(),AssetsFaultState.FINISH)){
            BusinessException.throwException("状态不能是已完成状态");
        }
        save(assetsFault);
    }

    @Override
    public void update(UpdateAssetsFaultDto updateAssetsFaultDto) {
        AssetsFault assetsFault = new AssetsFault();
        BeanUtils.copyProperties(updateAssetsFaultDto,assetsFault);
        if (Objects.equals(true,updateAssetsFaultDto.getState())) {
            assetsFault.setFinishTime(LocalDateTime.now());
        }
        assertUserExist(assetsFault.getDeclarantUserId());
        assertAssetsExist(assetsFault.getAssetsId());
        if (Objects.equals( assetsFault.getState(), AssetsFaultState.FINISH)) {
            assetsFault.setFinishTime(LocalDateTime.now());
        }
        update(assetsFault);
    }

    private void assertAssetsExist(Long id) {
        Assets assets = this.assetsService.findById(id);
        if (Objects.isNull(assets) ){
            BusinessException.throwException("该资产不存在");
        }
    }


    private void assertUserExist(Long id) {
        User user = this.userExposeService.findById(id);
        if (Objects.isNull(user) ){
            BusinessException.throwException("申报人不存在");
        }
    }
}
