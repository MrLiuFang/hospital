package com.lion.manage.service.assets;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.AssetsBorrow;
import com.lion.manage.entity.assets.dto.AddAssetsBorrowDto;
import com.lion.manage.entity.assets.dto.ReturnAssetsBorrowDto;
import com.lion.manage.entity.assets.vo.DetailsAssetsBorrowVo;
import com.lion.manage.entity.assets.vo.ListAssetsBorrowVo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:16
 */
public interface AssetsBorrowService extends BaseService<AssetsBorrow> {

    /**
     * 新增资产借用
     * @param addAssetsBorrowDto
     */
    public void add(AddAssetsBorrowDto addAssetsBorrowDto);

    /**
     * 列表
     *
     * @param name
     * @param borrowUserId
     * @param assetsTypeId
     * @param departmentId
     * @param assetsId
     * @param startDateTime
     * @param endDateTime
     * @param isReturn
     * @param lionPage
     * @return
     */
    IPageResultData<List<ListAssetsBorrowVo>> list(String name, Long borrowUserId, Long assetsTypeId, Long departmentId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage);

    /**
     * 导出
     * @param name
     * @param borrowUserId
     * @param assetsTypeId
     * @param departmentId
     * @param assetsId
     * @param startDateTime
     * @param endDateTime
     * @param isReturn
     */
    void export(String name, Long borrowUserId, Long assetsTypeId, Long departmentId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn) throws IOException, IllegalAccessException;

    /**
     * 修改资产借用(归还)
     * @param returnAssetsBorrowDto
     */
    public void returnAssetsBorrow(ReturnAssetsBorrowDto returnAssetsBorrowDto);

    /**
     * 最后一次借用
     * @param assetsId
     * @return
     */
    public DetailsAssetsBorrowVo lastDetails(Long assetsId);
}
