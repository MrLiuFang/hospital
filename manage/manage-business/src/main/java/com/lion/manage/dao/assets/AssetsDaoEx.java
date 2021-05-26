package com.lion.manage.dao.assets;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.manage.entity.assets.vo.ListAssetsBorrowVo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午11:17
 */
public interface AssetsDaoEx {

    Page list(Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage);
}
