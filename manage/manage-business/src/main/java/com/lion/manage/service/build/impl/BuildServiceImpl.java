package com.lion.manage.service.build.impl;

import com.lion.common.constants.RedisConstants;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.exception.BusinessException;
import com.lion.manage.dao.build.BuildDao;
import com.lion.manage.dao.build.BuildFloorDao;
import com.lion.manage.dao.region.RegionDao;
import com.lion.manage.entity.build.Build;
import com.lion.manage.entity.region.Region;
import com.lion.manage.service.build.BuildService;
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
    private RegionService regionService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void delete(List<DeleteDto> deleteDtoList) {
        deleteDtoList.forEach(deleteDto -> {
            this.deleteById(deleteDto.getId());
            buildFloorDao.deleteByBuildId(deleteDto.getId());
            List<Region> listRegion = regionDao.findByBuildId(deleteDto.getId());
            List<DeleteDto> regionDeleteDtoList = new ArrayList<DeleteDto>();
            listRegion.forEach(region -> {
                DeleteDto dto = new DeleteDto();
                dto.setId(region.getId());
                regionDeleteDtoList.add(dto);
            });
            if (regionDeleteDtoList.size()>0) {
                regionService.delete(regionDeleteDtoList);
            }
            redisTemplate.delete(RedisConstants.BUILD+deleteDto.getId());
        });
    }

    @Override
    public <S extends Build> S save(S entity) {
        assertNameExist(entity.getName(),null);
        entity = super.save(entity);
        persistenceRedis(entity);
        return entity;
    }

    @Override
    public void update(Build entity) {
        assertNameExist(entity.getName(),entity.getId());
        super.update(entity);
        persistenceRedis(entity);
    }

    private void assertNameExist(String name, Long id) {
        Build build = buildDao.findFirstByName(name);
        if ((Objects.isNull(id) && Objects.nonNull(build)) || (Objects.nonNull(id) && Objects.nonNull(build) && !Objects.equals(build.getId(),id)) ){
            BusinessException.throwException(MessageI18nUtil.getMessage("2000076"));
        }
    }

    private void persistenceRedis(Build build){
        redisTemplate.opsForValue().set(RedisConstants.BUILD+build.getId(),build,5, TimeUnit.MINUTES);
    }

}
