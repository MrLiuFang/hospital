package com.lion.manage.service.build.impl;

import com.lion.common.constants.RedisConstants;
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
import com.lion.manage.service.region.RegionService;
import com.lion.utils.MessageI18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RegionService regionService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<BuildFloor> find(Long buildId) {
        return buildFloorDao.findByBuildId(buildId);
    }

    @Override
    public void update(BuildFloor entity) {
        assertNameExist(entity.getName(),entity.getBuildId(),entity.getId());
        persistenceRedis(entity);
        super.update(entity);
    }

    @Override
    public <S extends BuildFloor> S save(S entity) {
        assertNameExist(entity.getName(),entity.getBuildId(),null);
        entity = super.save(entity);
        persistenceRedis(entity);
        return entity;
    }

    private void assertNameExist(String name, Long buildId,Long id) {
        BuildFloor buildFloor = buildFloorDao.findFirstByBuildIdAndName(buildId,name);
        if ((Objects.isNull(id) && Objects.nonNull(buildFloor))||(Objects.nonNull(id) && Objects.nonNull(buildFloor) && !Objects.equals(buildFloor.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000075"));
        }
    }

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            deleteById(deleteDto.getId());
            List<Region> listRegion = regionDao.findByBuildFloorId(deleteDto.getId());
            List<DeleteDto> regionDeleteDtoList = new ArrayList<DeleteDto>();
            listRegion.forEach(region -> {
                DeleteDto dto = new DeleteDto();
                dto.setId(region.getId());
                regionDeleteDtoList.add(dto);
            });
            if (regionDeleteDtoList.size()>0) {
                regionService.delete(regionDeleteDtoList);
            }
            redisTemplate.delete(RedisConstants.BUILD_FLOOR+deleteDto.getId());
        });
    }

    private void persistenceRedis(BuildFloor buildFloor){
        redisTemplate.opsForValue().set(RedisConstants.BUILD_FLOOR+buildFloor.getId(),buildFloor,RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
    }
}
