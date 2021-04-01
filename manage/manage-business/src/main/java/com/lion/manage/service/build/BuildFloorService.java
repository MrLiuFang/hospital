package com.lion.manage.service.build;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.build.BuildFloor;
import sun.security.ec.CurveDB;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:06
 */
public interface BuildFloorService extends BaseService<BuildFloor> {

    /**
     * 根据建筑查询
     * @param buildId
     * @return
     */
    public List<BuildFloor> find(Long buildId);

    /**
     * 删除楼层
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
