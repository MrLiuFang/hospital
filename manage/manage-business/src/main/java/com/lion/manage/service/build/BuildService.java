package com.lion.manage.service.build;

import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.build.Build;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:04
 */
public interface BuildService extends BaseService<Build> {

    /**
     * 删除
     * @param deleteDtoList
     */
    public void delete(List<DeleteDto> deleteDtoList);
}
