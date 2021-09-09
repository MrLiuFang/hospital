package com.lion.manage.service.region.impl;
//
//import com.lion.core.service.impl.BaseServiceImpl;
//import com.lion.manage.dao.region.RegionExposeObjectDao;
//import com.lion.manage.entity.enums.ExposeObject;
//import com.lion.manage.entity.region.RegionExposeObject;
//import com.lion.manage.service.region.RegionExposeObjectService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author Mr.Liu
// * @Description:
// * @date 2021/4/1上午11:09
// */
//@Service
//public class RegionExposeObjectServiceImpl extends BaseServiceImpl<RegionExposeObject> implements RegionExposeObjectService {
//
//    @Autowired
//    private RegionExposeObjectDao regionExposeObjectDao;
//
//    @Override
//    @Transactional
//    public void save(Long regionId, List<ExposeObject> list) {
//        if (Objects.nonNull(regionId)){
//            regionExposeObjectDao.deleteByRegionId(regionId);
//        }else {
//            return;
//        }
//        List<RegionExposeObject> regionExposeObjects = new ArrayList<>();
//        if (Objects.nonNull(list) && list.size()>0) {
//            list.forEach(exposeObject -> {
//                RegionExposeObject regionExposeObject = new RegionExposeObject();
//                regionExposeObject.setRegionId(regionId);
//                regionExposeObject.setExposeObject(exposeObject);
//                save(regionExposeObject);
//            });
//        }
//    }
//
//    @Override
//    public List<RegionExposeObject> find(Long regionId) {
//        return regionExposeObjectDao.findByRegionId(regionId);
//    }
//}
