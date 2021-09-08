package com.lion.manage.service.region;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.dto.AddAssetsTypeDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsTypeDto;
import com.lion.manage.entity.assets.vo.ListAssetsTypeVo;
import com.lion.manage.entity.region.RegionType;
import com.lion.manage.entity.region.dto.AddRegionTypeDto;
import com.lion.manage.entity.region.dto.UpdateRegionDto;
import com.lion.manage.entity.region.vo.ListRegionTypeVo;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 上午8:50
 */
public interface RegionTypeService extends BaseService<RegionType> {

    /**
     * 新增
     * @param addRegionTypeDto
     */
    public void add(AddRegionTypeDto addRegionTypeDto);

    /**
     * 修改
     * @param updateRegionDto
     */
    public void update(UpdateRegionDto updateRegionDto);

    /**
     * 删除
     * @param deleteDto
     */
    public void delete(List<DeleteDto> deleteDto);

    /**
     * 列表
     * @param name
     * @param LionPage
     * @return
     */
    public IPageResultData<List<ListRegionTypeVo>> list(String name, LionPage LionPage);
}
