package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.DeviceDataDto;
import com.lion.device.entity.tag.Tag;
import com.lion.manage.entity.assets.Assets;
import com.lion.upms.entity.user.User;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/15 下午2:41
 **/
public interface CommonService {

    /**
     * 记录位置
     * @param deviceDataDto
     * @param user
     * @param regionId
     * @throws JsonProcessingException
     */
    public void position(DeviceDataDto deviceDataDto, User user, Long regionId) throws JsonProcessingException;

    /**
     * 记录位置
     * @param deviceDataDto
     * @param tag
     * @param regionId
     * @throws JsonProcessingException
     */
    public void position(DeviceDataDto deviceDataDto, Tag tag,  Long regionId) throws JsonProcessingException;

    /**
     * 记录位置
     * @param deviceDataDto
     * @param assets
     * @param regionId
     * @throws JsonProcessingException
     */
    public void position(DeviceDataDto deviceDataDto, Assets assets, Long regionId) throws JsonProcessingException;

}
