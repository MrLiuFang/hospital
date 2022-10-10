package com.lion.device.dao.device;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.device.entity.device.WarningBell;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/8 下午1:56
 */
public interface WarningBellDao extends BaseDao<WarningBell> {

    @Modifying
    @Transactional
    @Query(" update WarningBell set regionId = null where regionId =:regionId")
    public int updateRegionIdIsNull(Long regionId);

    @Modifying
    @Transactional
    @Query(" update WarningBell set regionId = :regionId where id in :ids")
    public int updateRegionId(Long regionId,List<Long> ids);

    List<WarningBell> findByRegionId(Long regionId);

}
