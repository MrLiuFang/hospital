package com.lion.manage.service.build.impl;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.build.BuildFloorDao;
import com.lion.manage.dao.region.RegionCctvDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.dao.region.RegionExposeObjectDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.build.BuildFloor;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.build.BuildFloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:06
 */
@Service
public class BuildFloorServiceImpl extends BaseServiceImpl<BuildFloor> implements BuildFloorService {

    @Autowired
    private BuildFloorDao  buildFloorDao;

    @Autowired
    private RegionDao regionDao;

    @Autowired
    private RegionCctvDao regionCctvDao;

    @Autowired
    private RegionExposeObjectDao regionExposeObjectDao;


    @Override
    public List<BuildFloor> find(Long buildId) {
        return buildFloorDao.findByBuildId(buildId);
    }

    @Override
    public void update(BuildFloor entity) {
        assertNameExist(entity.getName(),entity.getBuildId(),entity.getId());
        super.update(entity);
    }

    @Override
    public <S extends BuildFloor> S save(S entity) {
        assertNameExist(entity.getName(),entity.getBuildId(),null);
        return super.save(entity);
    }

    private void assertNameExist(String name, Long buildId,Long id) {
        BuildFloor buildFloor = buildFloorDao.findFirstByBuildIdAndName(buildId,name);
        if (Objects.isNull(id) && Objects.nonNull(buildFloor) ){
            BusinessException.throwException("该建筑已经存在该楼层");
        }
        if (Objects.nonNull(id) && Objects.nonNull(buildFloor) && !buildFloor.getId().equals(id)){
            BusinessException.throwException("该建筑已经存在该楼层");
        }
    }

    @Override
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
            List<Region> listRegion = regionDao.findByBuildFloorId(deleteDto.getId());
            listRegion.forEach(region -> {
                regionDao.delete(region);
                regionCctvDao.deleteByRegionId(region.getId());
                regionExposeObjectDao.deleteByRegionId(region.getId());
            });
        });
    }
}
