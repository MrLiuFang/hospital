package com.lion.event.service.impl;

import com.lion.common.enums.Type;
import com.lion.constant.SearchConstant;
import com.lion.core.IPageResultData;
import com.lion.core.LionPage;
import com.lion.core.PageResultData;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.dao.HumitureRecordDao;
import com.lion.event.entity.HumitureRecord;
import com.lion.event.entity.vo.ListHumitureRecordVo;
import com.lion.event.service.HumitureRecordService;
import com.lion.event.utils.ExcelColumn;
import com.lion.event.utils.ExportExcelUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/17 下午9:17
 **/
@Service
public class HumitureRecordServiceImpl implements HumitureRecordService {

    @Autowired
    private HumitureRecordDao humitureRecordDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private HttpServletResponse response;

    @Override
    public void save(HumitureRecord humitureRecord) {
        humitureRecordDao.save(humitureRecord);
    }

    @Override
    public IPageResultData<List<ListHumitureRecordVo>> temperatureHumidityList(Long regionId, Long departmentId, String deviceCode, LocalDateTime startDateTime, LocalDateTime endDateTime, String ids, LionPage lionPage) {
        Map<String, Object> searchParameter = new HashMap<>();
        if (StringUtils.hasText(deviceCode)){
            searchParameter.put(SearchConstant.LIKE+"_deviceCode",deviceCode);
        }
        List<Tag> tagList = tagExposeService.find(searchParameter);
        List<Long> tagIds = new ArrayList<>();
        tagList.forEach(tag -> {
            tagIds.add(tag.getId());
        });
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(ids)){
            List<String> _ids = new ArrayList<>();
            String[] str = ids.split(",");
            for (String id:str) {
                _ids.add(id);
            }
            if (_ids.size()>0){
                criteria.and("_id").in(_ids);
            }
        }
        if (tagIds.size()>0) {
            criteria.and("ti").in(tagIds);
        }
        if (Objects.nonNull(regionId)){
            criteria.and("ri").is(regionId);
        }
        if (Objects.nonNull(departmentId)){
            criteria.and("di").is(departmentId);
        }
        if (Objects.isNull(startDateTime)) {
            startDateTime = LocalDateTime.now().minusDays(30);
        }
        if (Objects.nonNull(startDateTime) && Objects.nonNull(endDateTime) ) {
            criteria.andOperator( Criteria.where("ddt").gte(startDateTime) ,Criteria.where("ddt").lte(endDateTime));
        }else if (Objects.nonNull(startDateTime) &&  Objects.isNull(endDateTime)) {
            criteria.and("ddt").gte(startDateTime);
        }else if (Objects.isNull(startDateTime) &&  Objects.nonNull(endDateTime)) {
            criteria.and("ddt").lte(endDateTime);
        }
        query.addCriteria(criteria);
        query.with(lionPage);
        query.with(Sort.by(Sort.Direction.DESC,"ddt"));
        List<HumitureRecord> items = mongoTemplate.find(query, HumitureRecord.class);
        List<ListHumitureRecordVo> returnList = new ArrayList<>();
        items.forEach(humitureRecord -> {
            ListHumitureRecordVo vo = new ListHumitureRecordVo();
            BeanUtils.copyProperties(humitureRecord,vo);
            com.lion.core.Optional<Tag> optionalTag = tagExposeService.findById(humitureRecord.getTi());
            if (optionalTag.isPresent()){
                Tag tag = optionalTag.get();
                vo.setTagCode(tag.getTagCode());
                vo.setDeviceName(tag.getDeviceName());
                vo.setDeviceCode(tag.getDeviceCode());
                vo.setType(Type.instance(humitureRecord.getTyp()));
            }
            returnList.add(vo);
        });
        return new PageResultData<>(returnList,lionPage,0L);
    }

    @Override
    public void temperatureHumidityListExport(Long regionId, Long departmentId, String deviceCode, LocalDateTime startDateTime, LocalDateTime endDateTime, String ids, LionPage lionPage) throws IOException, IllegalAccessException {
        IPageResultData<List<ListHumitureRecordVo>> pageResultData = temperatureHumidityList(regionId,departmentId,deviceCode,startDateTime,endDateTime,ids , lionPage);
        List<ListHumitureRecordVo> list = pageResultData.getData();
        List<ExcelColumn> excelColumn = new ArrayList<ExcelColumn>();
        excelColumn.add(ExcelColumn.build("device name", "deviceName"));
        excelColumn.add(ExcelColumn.build("device code", "deviceCode"));
        excelColumn.add(ExcelColumn.build("tag code", "tagCode"));
        excelColumn.add(ExcelColumn.build("department name", "dn"));
        excelColumn.add(ExcelColumn.build("region name", "rn"));
        excelColumn.add(ExcelColumn.build("date time", "ddt"));
        excelColumn.add(ExcelColumn.build("temperature", "t"));
        excelColumn.add(ExcelColumn.build("humidity", "h"));
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("temperatureHumidity.xls", "UTF-8"));
        new ExportExcelUtil().export(list, response.getOutputStream(), excelColumn);
    }

    @Override
    public HumitureRecord findLast(Long tagId) {
        List<HumitureRecord> items = find(tagId,null,null,0,1);
        if (Objects.nonNull(items) && items.size()>0){
            return items.get(0);
        }
        return null;
    }

    @Override
    public List<HumitureRecord> find(Long tagId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<HumitureRecord> items = find(tagId,startDateTime,endDateTime,0,Integer.MAX_VALUE);
        return items;
    }

    private List<HumitureRecord> find(Long tagId, LocalDateTime startDateTime, LocalDateTime endDateTime,int page,int size) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (Objects.nonNull(tagId)) {
            criteria.and("ti").is(tagId);
        }
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(startDateTime)) {
            startDateTime =  now.minusDays(30);
        }
        if (Objects.isNull(endDateTime)) {
            endDateTime =  now;
        }
        criteria.andOperator( Criteria.where("ddt").gte(startDateTime) ,Criteria.where("ddt").lte(endDateTime));
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(page,size,Sort.by(Sort.Order.desc("ddt")));
        query.with(pageRequest);
        List<HumitureRecord> items = mongoTemplate.find(query, HumitureRecord.class);

        return items;
    }
}
