package com.lion.manage.dao.assets.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.lion.core.LionPage;
import com.lion.core.persistence.curd.BaseDao;
import com.lion.manage.dao.assets.AssetsDaoEx;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.enums.State;
import org.checkerframework.checker.i18nformatter.qual.I18nFormat;
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
    public Page list(String name, Long borrowUserId, List<Long> departmentIds, Long assetsTypeId, Long assetsId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isReturn, LionPage lionPage) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select new com.lion.core.persistence.curd.MoreEntity(a,ab) from Assets a join AssetsBorrow ab on a.id = ab.assetsId where 1=1");
        if (StringUtils.hasText(name)){
            sb.append(" and a.name like :name ");
            searchParameter.put("name",name);
        }
        if (Objects.nonNull(departmentIds) && departmentIds.size()>0) {
            sb.append(" and a.departmentId in :departmentIds ");
            searchParameter.put("departmentIds",departmentIds);
        }
        if (Objects.nonNull(assetsTypeId)) {
            sb.append(" and a.assets_type_id = :assetsId ");
            searchParameter.put("assetsId", assetsId);
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
            sb.append(" and ab.returnUserId is not null");
        }else if (Objects.nonNull(isReturn) && Objects.equals(isReturn,false)) {
            sb.append(" and ab.returnUserId is null");
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

    @Override
    public Integer count(Long departmentId, State deviceState, List<Long> assetsIds) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> searchParameter = new HashMap<String, Object>();
        sb.append(" select count(a.id) from Assets a where 1=1 ");
        if (Objects.nonNull(departmentId)){
            sb.append(" and a.departmentId = :departmentId ");
            searchParameter.put("departmentId",departmentId);
        }
        if (Objects.nonNull(deviceState)){
            sb.append(" and a.deviceState = :deviceState ");
            searchParameter.put("deviceState",deviceState);
        }
        if (Objects.nonNull(assetsIds) && assetsIds.size()>0) {
            sb.append(" and a.id in :assetsIds ");
            searchParameter.put("assetsIds",assetsIds);
        }
        List<Map> list = (List<Map>) baseDao.findAll(sb.toString(),searchParameter);
        if (Objects.nonNull(list) && list.size()>0) {
            return Integer.valueOf(String.valueOf(list.get(0)));
        }
        return 0;
    }
}
