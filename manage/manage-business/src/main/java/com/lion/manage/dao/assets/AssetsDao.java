package com.lion.manage.dao.assets;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.assets.Assets;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/6下午3:12
 */
public interface AssetsDao extends BaseDao<Assets> {

    /**
     * 根据名称查询资产
     * @param name
     * @return
     */
    public Assets findFirstByName(String name);

    /**
     * 根据编码查询资产
     * @param code
     * @return
     */
    public Assets findFirstByCode(String code);

    /**
     * 根据标签编码查询资产
     * @param tagCode
     * @return
     */
    public Assets findFirstByTagCode(String tagCode);
}
