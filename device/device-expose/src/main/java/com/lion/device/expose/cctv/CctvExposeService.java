package com.lion.device.expose.cctv;

import com.lion.core.service.BaseService;
import com.lion.device.entity.cctv.Cctv;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1下午9:33
 */
public interface CctvExposeService extends BaseService<Cctv> {

    /**
     * 根据id数组查询
     * @param ids
     * @return
     */
    public List<Cctv> find(List<Long> ids);
}
