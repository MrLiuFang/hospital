package com.lion.manage.dao.region;

import com.lion.manage.entity.region.Region;

import java.util.List;

public interface RegionDaoEx {

    public List<Region> find(String name,String code,Long departmentId);
}
