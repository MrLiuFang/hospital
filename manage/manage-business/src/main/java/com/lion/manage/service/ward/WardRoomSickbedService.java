package com.lion.manage.service.ward;

import com.lion.core.LionPage;
import com.lion.core.service.BaseService;
import com.lion.manage.entity.ward.WardRoomSickbed;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/1上午11:17
 */
public interface WardRoomSickbedService extends BaseService<WardRoomSickbed> {

    /**
     * 保存病床
     * @param wardRoomSickbedDto
     * @param wardRoomId
     */
    public void save(List<? extends WardRoomSickbed> wardRoomSickbedDto, Long wardRoomId);

    /**
     * 根据病房房间查询
     * @param wardRoomId
     * @return
     */
    public List<WardRoomSickbed> find(Long wardRoomId);

    /**
     * 列表
     *
     * @param bedCode
     * @param departmentId
     * @param wardId
     * @param wardRoomId
     * @param lionPage
     * @return
     */
    public Page<WardRoomSickbed> list(String bedCode,Long departmentId, Long wardId, Long wardRoomId, LionPage lionPage);
}
