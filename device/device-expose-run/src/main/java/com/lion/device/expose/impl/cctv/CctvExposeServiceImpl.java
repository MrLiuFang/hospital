package com.lion.device.expose.impl.cctv;

import com.lion.core.service.impl.BaseServiceImpl;
import com.lion.device.dao.cctv.CctvDao;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.expose.cctv.CctvExposeService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:34
 */
@DubboService(interfaceClass = CctvExposeService.class)
public class CctvExposeServiceImpl extends BaseServiceImpl<Cctv> implements CctvExposeService {

    @Autowired
    private CctvDao cctvDao;


    @Override
    public List<Cctv> find(List<Long> ids) {
        return cctvDao.findByIdIn(ids);
    }

    @Override
    public Cctv find(String code) {
        return cctvDao.findFirstByCode(code);
    }

    @Override
    @Transactional
    public void relationPosition(List<Long> cctvIds, Long buildId, Long buildFloorId, Long regionId, Long departmentId) {
        cctvDao.updateRegionIsNull(regionId);
        if(Objects.nonNull(cctvIds)) {
            cctvIds.forEach(id -> {
                com.lion.core.Optional<Cctv> optional = findById(id);
                if (optional.isPresent()) {
                    Cctv cctv = optional.get();
                    cctv.setBuildId(buildId);
                    cctv.setBuildFloorId(buildFloorId);
                    cctv.setRegionId(regionId);
                    cctv.setDepartmentId(departmentId);
                    update(cctv);
                }
            });
        }
    }

    @Override
    public Integer count(Long departmentId) {
        return cctvDao.countByDepartmentId(departmentId);
    }

    @Override
    public List<Cctv> findRegionId(Long regionId) {
        return cctvDao.findByRegionId(regionId);
    }

    @Override
    public List<Cctv> findDepartmentId(Long departmentId, String keyword) {
        return cctvDao.find(departmentId,keyword,keyword,keyword,keyword);
    }
}
