package com.lion.manage.dao.assets.impl;

import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.assets.AssetsDaoEx;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.AssetsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/26 上午11:18
 */
public class AssetsDaoImpl implements AssetsDaoEx {

    @Autowired
    private BaseDao<Assets> baseDao;


    @Override
    public Page list(String name, Long borrowUserId, List<Long> departmentIds, AssetsType type, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select new com.lion.core.persistence.curd.MoreEntity(a,ab) from Assets a join AssetsBorrow ab on a.id = ab.assetsId where 1=1");
        if (StringUtils.hasText(name)){
            sb.append(" and a.name like :name ");
            searchParameter.put("name","%"+name+"%");
        }
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            sb.append(" and a.departmentId in :departmentIds ");
            searchParameter.put("departmentIds",departmentIds);
        }
        if (Objects.nonNull(type)) {
            sb.append(" and a.type = :type ");
            searchParameter.put("type",type);
        }
        if (Objects.nonNull(assetsId)) {
            sb.append(" and a.id = :assetsId ");
            searchParameter.put("assetsId",assetsId);
        }
        if (Objects.nonNull(borrowUserId)) {
            sb.append(" and ab.borrowUserId = :borrowUserId ");
            searchParameter.put("borrowUserId",borrowUserId);
        }
        if (Objects.nonNull(isReturn) && Objects.equals(isReturn,true)) {
            sb.append(" ab.returnUserId is not null");
        }else if (Objects.nonNull(isReturn) && Objects.equals(isReturn,false)) {
            sb.append(" ab.returnUserId is not null");
        }
        if (Objects.nonNull(startDateTime)) {
            sb.append(" and ab.startDateTime >= :startDateTime ");
            searchParameter.put("startDateTime",startDateTime);
        }
        if (Objects.nonNull(endDateTime)) {
            sb.append(" and ab.endDateTime <= :endDateTime ");
            searchParameter.put("endDateTime",endDateTime);
        }

        sb.append(" order by a.createDateTime ");
        return baseDao.findNavigator(lionPage, sb.toString(), searchParameter);
    }
}
