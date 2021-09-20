package com.lion.person.service.person;
//
//import com.lion.core.IPageResultData;
//import com.lion.core.LionPage;
//import com.lion.core.service.BaseService;
//import com.lion.person.entity.person.TempLeave;
//import com.lion.person.entity.person.dto.AddTempLeaveDto;
//import com.lion.person.entity.person.dto.AdvanceOverTempLeaveDto;
//import com.lion.person.entity.person.vo.ListTempLeaveVo;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * @description:
// * @author: Mr.Liu
// * @time: 2021/5/25 下午9:24
// */
//public interface TempLeaveService extends BaseService<TempLeave> {
//
//    /**
//     * 新增临时离开
//     * @param addTempLeaveDto
//     */
//    public void addTempLeave(AddTempLeaveDto addTempLeaveDto);
//
//    /**
//     * 提前结束临时离开权限
//     * @param advanceOverTempLeaveDto
//     */
//    public void advanceOverTempLeave(AdvanceOverTempLeaveDto advanceOverTempLeaveDto);
//
//    /**
//     * 列表
//     *
//     * @param tagCode
//     * @param departmentId
//     * @param patientId
//     * @param userId
//     * @param startDateTime
//     * @param endDateTime
//     * @param lionPage
//     * @return
//     */
//    public IPageResultData<List<ListTempLeaveVo>> list( String tagCode,  Long departmentId, Long patientId,Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, LionPage lionPage);
//}
