package com.lion.device.dao.cctv;

import com.lion.device.entity.cctv.Cctv;

import java.util.List;

public interface CctvDaoEx {

    public List<Cctv> find(Long departmentId, String name,String code,String model,String cctvId);
}
