package com.lion.event.service;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.event.entity.Wash;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/5 上午8:46
 **/
public interface WashService {

    /**
     * 保存
     * @param wash
     */
    public void save(Wash wash);

    /**
     * 列表
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<Wash>> list(Long userId,LocalDateTime startDateTime,LocalDateTime endDateTime, LionPage lionPage);
}
