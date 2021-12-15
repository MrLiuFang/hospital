package com.lion.manage.expose.region;

import com.lion.core.service.BaseService;
import com.lion.manage.entity.region.RegionCctv;
import org.bouncycastle.pqc.crypto.newhope.NHOtherInfoGenerator;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/9/30 上午10:51
 */
public interface RegionCctvExposeService extends BaseService<RegionCctv> {

    /**
     * 根据区域统计
     * @param regionId
     * @return
     */
    public int count(Long regionId);

    /**
     * 根据CCTV
     * @param cctvId
     * @return
     */
    public RegionCctv find(Long cctvId);
}
