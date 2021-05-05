package com.lion.device.dao.cctv;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.device.entity.cctv.Cctv;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午10:24
 */
public interface CctvDao extends BaseDao<Cctv> {

    /**
     * 根据id数组查询
     * @param ids
     * @return
     */
    public List<Cctv> findByIdIn(List<Long> ids);

}
