package com.lion.manage.service.assets;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsFault;
import com.lion.manage.entity.assets.dto.AddAssetsFaultDto;
import com.lion.manage.entity.assets.dto.UpdateAssetsFaultDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsFaultVo;
import com.lion.manage.entity.assets.vo.ListAssetsFaultVo;
import com.lion.manage.entity.enums.AssetsFaultState;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:19
 */
public interface AssetsFaultService extends BaseService<AssetsFault> {

    /**
     * 新增资产故障
     * @param addAssetsFaultDto
     */
    public void add(AddAssetsFaultDto addAssetsFaultDto);

    /**
     * 修改资产故障
     * @param updateAssetsFaultDto
     */
    public void update(UpdateAssetsFaultDto updateAssetsFaultDto);

    /**
     * 列表
     * @param departmentId
     * @param state
     * @param assetsId
     * @param code
     * @param assetsCode
     * @param keyword
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListAssetsFaultVo>> list(Long departmentId, AssetsFaultState state,  Long assetsId, String code, String assetsCode,String keyword,LocalDateTime startDateTime,LocalDateTime endDateTime,LionPage lionPage);

    /**
     * 导出
     *
     * @param departmentId
     * @param state
     * @param assetsId
     * @param code
     * @param assetsCode
     * @param keyword
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     */
    public void export(Long departmentId, AssetsFaultState state,  Long assetsId, String code, String assetsCode,String keyword,LocalDateTime startDateTime,LocalDateTime endDateTime,LionPage lionPage) throws IOException, IllegalAccessException;

    /**
     * 详情
     * @param id
     * @return
     */
    DetailsAssetsFaultVo details(Long id);

    List<AssetsFault> find(String keyword);
}
