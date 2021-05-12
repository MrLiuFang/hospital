package com.lion.manage.dao.work;

import com.lion.manage.entity.work.Work;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/5/12 下午10:28
 **/
public interface WorkDaoEx {

    /**
     * 分页查询
     * @param userId
     * @param startDateTime
     * @param endDateTime
     * @param page
     * @param size
     * @return
     */
    Page<Work> list(List<Long> userId, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size);
}
