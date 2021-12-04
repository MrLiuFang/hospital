package com.lion.person.service.person;

import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.common.dto.DeleteDto;
import com.lion.core.service.BaseService;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.person.entity.person.dto.*;
import com.lion.person.entity.person.vo.ListTemporaryPersonVo;
import com.lion.person.entity.person.vo.PatientDetailsVo;
import com.lion.person.entity.person.vo.TemporaryPersonDetailsVo;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description:
 * @author: Mr.Liu
 * @time: 2021/5/25 上午9:09
 */
public interface TemporaryPersonService extends BaseService<TemporaryPerson> {

    /**
     * 新增流动人员
     * @param addTemporaryPersonDto
     */
    public void add(AddTemporaryPersonDto addTemporaryPersonDto);

    /**
     * 修改流动人员
     * @param updateTemporaryPersonDto
     */
    public void update(UpdateTemporaryPersonDto updateTemporaryPersonDto);

    /**
     * 删除
     * @param deleteDtos
     * @return
     */
    public void delete(List<DeleteDto> deleteDtos);

    /**
     * 列表
     * @param name
     * @param isLeave
     * @param tagCode
     * @param startDateTime
     * @param endDateTime
     * @param lionPage
     * @return
     */
    public IPageResultData<List<ListTemporaryPersonVo>> list(String name,Boolean isLeave,String tagCode, LocalDateTime startDateTime,LocalDateTime endDateTime,LionPage lionPage);

    /**
     * 导出
     * @param name
     * @param isLeave
     * @param tagCode
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public void export(String name,Boolean isLeave,String tagCode, LocalDateTime startDateTime,LocalDateTime endDateTime) throws IOException, IllegalAccessException;
    /**
     * 详情
     * @param id
     * @return
     */
    public TemporaryPersonDetailsVo details(Long id);

    /**
     * 登出
     * @param temporaryPersonLeaveDto
     */
    public void leave(TemporaryPersonLeaveDto temporaryPersonLeaveDto);

}
