package com.lion.manage.dao.rule;

import com.lion.core.persistence.curd.BaseDao;
import com.lion.core.persistence.entity.BaseEntity;
import com.lion.manage.entity.rule.WashTemplateItem;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @author Mr.Liu
 * @description $
 * @createDateTime 2021/9/7 上午10:52
 */
public interface WashTemplateItemDao extends BaseDao<WashTemplateItem> {

    /**
     * 根据规则模板删除
     * @param washTemplateId
     * @return
     */
    public int deleteByWashTemplateId(Long washTemplateId);

    /**
     * 根据规则模板查询
     * @param washTemplateId
     * @return
     */
    public List<WashTemplateItem> findByWashTemplateId(Long washTemplateId);
}
