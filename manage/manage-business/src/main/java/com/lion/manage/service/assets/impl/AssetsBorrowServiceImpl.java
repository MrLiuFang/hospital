package com.lion.manage.service.assets.impl;

import com.lion.core.service.BaseService;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.assets.AssetsBorrowDao;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.dto.AddAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsBorrowDto;
import com.lion.manage.entity.department.Department;
import com.lion.manage.service.assets.AssetsBorrowService;
import com.lion.manage.service.assets.AssetsService;
import com.lion.upms.entity.user.User;
import com.lion.upms.expose.user.UserExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public void add(AddAssetsBorrowDto addAssetsBorrowDto) {
        AssetsBorrow assetsBorrow = new AssetsBorrow();
        BeanUtils.copyProperties(addAssetsBorrowDto,assetsBorrow);
        assertUserExist(assetsBorrow.getBorrowUserId());
        assertAssetsExist(assetsBorrow.getAssetsId());
        save(assetsBorrow);
    }

    @Override
    public void update(UpdateAssetsBorrowDto updateAssetsBorrowDto) {
        AssetsBorrow assetsBorrow = new AssetsBorrow();
        BeanUtils.copyProperties(updateAssetsBorrowDto,assetsBorrow);
        assertUserExist(assetsBorrow.getReturnUserId());
        assertAssetsExist(assetsBorrow.getAssetsId());
        if (Objects.nonNull(assetsBorrow.getReturnUserId())) {
            assetsBorrow.setReturnTime(LocalDateTime.now());
        }
        update(assetsBorrow);
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
            BusinessException.throwException("借用人/归还人不存在");
        }
    }

}
