package com.lion.person.service.person.impl;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.person.dao.person.RestrictedAreaDao;
import com.lion.person.entity.enums.PersonType;
import com.lion.person.entity.person.RestrictedArea;
import com.lion.person.service.person.RestrictedAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:08
 */
@Service
public class RestrictedAreaServiceImpl extends BaseServiceImpl<RestrictedArea> implements RestrictedAreaService {

    @Autowired
    private RestrictedAreaDao restrictedAreaDao;

    @Override
    @Transactional
    public void add(List<Long> regionId, PersonType type, Long personId) {
        if (Objects.nonNull(personId)) {
            restrictedAreaDao.deleteByPersonId(personId);
        }
        if (Objects.nonNull(regionId) && regionId.size()>0 && Objects.nonNull(type) && Objects.nonNull(personId)) {
            regionId.forEach(id->{
                RestrictedArea restrictedArea = new RestrictedArea();
                restrictedArea.setType(type);
                restrictedArea.setPersonId(personId);
                restrictedArea.setRegionId(id);
                save(restrictedArea);
            });
        }
    }

    @Override
    @Transactional
    public void delete(Long personId) {
        restrictedAreaDao.deleteByPersonId(personId);
    }

    @Override
    public List<RestrictedArea> find(Long personId, PersonType type) {
        return restrictedAreaDao.findByPersonIdAndType(personId, type);
    }
}
