package com.lion.event.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.utils.RedisUtil;
import com.lion.core.Optional;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.device.Device;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.event.entity.SystemAlarm;
import com.lion.event.entity.WashEvent;
import com.lion.event.service.SaveCctvService;
import com.lion.manage.entity.region.Region;
import com.lion.manage.expose.region.RegionExposeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class SaveCctvServiceImpl implements SaveCctvService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @DubboReference
    private RegionExposeService regionExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cctv.host}")
    private String cctvHost;

    @Value("${cctv.path}")
    private String cctvPath;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void saveCctv(UserLastWashDto userLastWashDto) {
        if (Objects.nonNull(userLastWashDto) && Objects.nonNull(userLastWashDto.getDateTime()) && Objects.nonNull(userLastWashDto.getUserId()) ) {
            Device device = redisUtil.getDevice(userLastWashDto.getMonitorId());
            if (Objects.isNull(device)) {
                return;
            }
            Document match = new Document();
            match.put("pi",userLastWashDto.getUserId());
            match.put("ddt",userLastWashDto.getDateTime());
            match.put("sdt",userLastWashDto.getSystemDateTime());
            Query query = new BasicQuery(match);
            WashEvent washEvent = mongoTemplate.findOne(query, WashEvent.class);
            Optional<Region> optionalRegion = regionExposeService.findById(device.getRegionId());
            if (optionalRegion.isPresent()) {
                String cctvUrl = saveCctv(cctvExposeService.findRegionId(optionalRegion.get().getId()),userLastWashDto.getDateTime(),userLastWashDto.getDateTime().plusSeconds(userLastWashDto.getTime()));
                if (StringUtils.hasText(cctvUrl)) {
                    if (Objects.nonNull(washEvent)) {
                        Query queryUpdate = new Query();
                        queryUpdate.addCriteria(Criteria.where("_id").is(washEvent.get_id()));
                        Update update = new Update();
                        update.set("cctvUrl", cctvUrl);
                        mongoTemplate.updateFirst(queryUpdate, update, "wash_event");
                    }
                }
            }
        }
    }

    @Override
    public void saveCctv(SystemAlarm systemAlarm) {
        Optional<Region> optionalRegion = regionExposeService.findById(systemAlarm.getRi());
        if (optionalRegion.isPresent()) {
            String cctvUrl = saveCctv(cctvExposeService.findRegionId(optionalRegion.get().getId()),systemAlarm.getDt().minusSeconds(10),systemAlarm.getDt().plusSeconds(10));
            if (StringUtils.hasText(cctvUrl)) {
                Query queryUpdate = new Query();
                queryUpdate.addCriteria(Criteria.where("_id").is(systemAlarm.get_id()));
                Update update = new Update();
                update.set("cctvUrl", cctvUrl);
                mongoTemplate.updateFirst(queryUpdate, update, "system_alarm");
            }
        }
    }

    private String saveCctv(List<Cctv> cctvList,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = LocalDateTimeUtil.format(localDateTime, DatePattern.PURE_DATE_PATTERN);
        if (cctvList.size()>0) {
            String cctvUrl = "";
            for (Cctv cctv : cctvList) {
                String uuid = UUID.randomUUID().toString();
                String url = cctvHost + "/call/?type=Playback&cameraID=" + cctv.getCctvId() + "&startTime=" +startDateTime+ "&endTime=" + endDateTime + "&nvrIp=" + cctv.getIp() + "&nvrPort=" + cctv.getPort();
                //定义请求头的接收类型
                RequestCallback requestCallback = request -> request.getHeaders()
                        .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
                File file =new File(cctvPath + date);
                if (!file.exists()){
                    file.mkdirs();
                }
                String savePath = cctvPath + date + "/" + uuid + ".mp4";
                restTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
                    Files.copy(clientHttpResponse.getBody(), Paths.get(savePath), StandardCopyOption.REPLACE_EXISTING);
                    return null;
                });
                if (StringUtils.hasText(cctvUrl)) {
                    cctvUrl = cctvUrl + ",/cctv/"+ date + "/" + uuid + ".mp4";
                } else {
                    cctvUrl = "/cctv/"+date + "/" + uuid + ".mp4";
                }
            }
            return cctvUrl;
        }
        return "";
    }
}
