package com.lion.manage.service.assets;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsType;
import com.lion.manage.entity.assets.dto.AddAssetsTypeDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsTypeDto;
import com.lion.manage.entity.assets.vo.ListAssetsTypeVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午9:33
 */
public interface AssetsTypeService extends BaseService<AssetsType> {

    /**
     * 新增
     * @param addAssetsTypeDto
     */
    public void add(AddAssetsTypeDto addAssetsTypeDto);

    /**
     * 修改
     * @param updateAssetsTypeDto
     */
    public void update(UpdateAssetsTypeDto updateAssetsTypeDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

    /**
     * 列表
     * @param assetsTypeName
     * @param LionPage
     * @return
     */
    public IPageResultData<List<ListAssetsTypeVo>> list(String assetsTypeName, LionPage LionPage);
}
