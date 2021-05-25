package com.lion.person.service.person;

import com.lion.core.service.BaseService;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;

import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:08
 */
public interface RestrictedAreaService extends BaseService<RestrictedArea> {

    /**
     * 新增加限制区域
     * @param regionId
     * @param type
     * @param personId
     */
    public void add(List<Long> regionId, PersonType type,Long personId);

    /**
     * 根据人员删除
     * @param personId
     */
    public void delete(Long personId);
}
