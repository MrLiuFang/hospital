package com.lion.manage.service.build.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.build.BuildDao;
import com.lion.manage.dao.build.BuildFloorDao;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionExposeObjectDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.department.Department;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.build.BuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:05
 */
@Service
public class BuildServiceImpl extends BaseServiceImpl<Build> implements BuildService {

    @Autowired
    private BuildDao buildDao;

    @Autowired
    private BuildFloorDao buildFloorDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Autowired
    private RegionExposeObjectDao regionExposeObjectDao;

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            this.deleteById(deleteDto.getId());
            buildFloorDao.deleteByBuildId(deleteDto.getId());
            List<Region> listRegion = regionDao.findByBuildId(deleteDto.getId());
            listRegion.forEach(region -> {
                regionDao.delete(region);
                regionCctvDao.deleteByRegionId(region.getId());
                regionExposeObjectDao.deleteByRegionId(region.getId());
            });
        });
    }

    @Override
    public <S extends Build> S save(S entity) {
        assertNameExist(entity.getName(),null);
        return super.save(entity);
    }

    @Override
    public void update(Build entity) {
        assertNameExist(entity.getName(),entity.getId());
        super.update(entity);
    }

    private void assertNameExist(String name, Long id) {
        Build build = buildDao.findFirstByName(name);
        if (Objects.isNull(id) && Objects.nonNull(build) ){
            BusinessException.throwException("该建筑名称已存在");
        }
        if (Objects.nonNull(id) && Objects.nonNull(build) && !build.getId().equals(id)){
            BusinessException.throwException("该建筑名称已存在");
        }
    }

}
