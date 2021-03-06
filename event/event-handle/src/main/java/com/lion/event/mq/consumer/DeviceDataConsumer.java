package com.lion.event.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.common.constants.RedisConstants;
import com.lion.common.constants.TopicConstants;
import com.lion.common.dto.DeviceDataDto;
import com.lion.common.dto.UserLastWashDto;
import com.lion.common.enums.Type;
import com.lion.common.utils.RedisUtil;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.TagPurpose;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.event.service.*;
import com.lion.manage.entity.assets.Assets;
import com.lion.manage.entity.license.License;
import com.lion.person.entity.person.Patient;
import com.lion.person.entity.person.TemporaryPerson;
import com.lion.upms.entity.user.User;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author Mr.Liu
 * @Description
 **/

@Component
@RocketMQMessageListener(topic = TopicConstants.DEVICE_DATA,selectorExpression="*",consumerGroup = TopicConstants.DEVICE_DATA_CONSUMER_GROUP)
@Log
public class DeviceDataConsumer implements RocketMQListener<MessageExt> {


    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserWashService userWashService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private TemporaryPersonService temporaryPersonService;

    @Autowired
    private UserButtonService userButtonService;

    @Autowired
    private RecyclingBoxService recyclingBoxService;

    @Autowired
    private BatteryAlarmService batteryAlarmService;

    @Override
    public void onMessage(MessageExt messageExt) {
        License license = redisUtil.getLicense();
        if (Objects.isNull(license)){
            return;
        }
        String menu = license.getMenuList();

        try {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            log.info(msg);
            DeviceDataDto deviceDataDto = jacksonObjectMapper.readValue(msg, DeviceDataDto.class);
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            deviceDataDto.setSystemDateTime(LocalDateTime.parse(LocalDateTime.now().format(df),df));
            Device monitor = null;
            Device star = null;
            Tag tag = null;
            User user = null;
            Patient patient = null;
            TemporaryPerson temporaryPerson = null;
            Assets assets = null;
            if (Objects.nonNull(deviceDataDto.getMonitorId())) {
                monitor = redisUtil.getDevice(deviceDataDto.getMonitorId());
            }
            if (Objects.nonNull(deviceDataDto.getStarId())) {
                star = redisUtil.getDevice(deviceDataDto.getStarId());
            }
            if (Objects.isNull(monitor) && Objects.isNull(star)) {
                return;
            }
            if (Objects.nonNull(deviceDataDto.getTagId())) {
                tag = redisUtil.getTag(deviceDataDto.getTagId());
            }

            if (Objects.nonNull(tag)){
                redisTemplate.opsForValue().set(RedisConstants.LAST_DATA+String.valueOf(tag.getId()),LocalDateTime.now(), RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                redisTemplate.delete(RedisConstants.TAG_LOSE+tag.getId());
                Type type = redisUtil.getTagBindType(tag.getId());
                if (Objects.equals(type,Type.STAFF)) {
                    user = redisUtil.getUser(tag.getId());
                }else if (Objects.equals(type,Type.PATIENT)) {
                    patient = redisUtil.getPatientByTagId(tag.getId());
                }else if (Objects.equals(type,Type.MIGRANT)) {
                    temporaryPerson = redisUtil.getTemporaryPersonByTagId(tag.getId());
                }else if (Objects.equals(type,Type.ASSET)) {
                    assets = redisUtil.getAssets(tag.getId());
                }
            }else {
                return;
            }

            if (Objects.nonNull(user) && menu.indexOf("????????????")>-1){
                if ((Objects.nonNull(monitor) && !Objects.equals(monitor.getDeviceClassify(), DeviceClassify.HAND_WASHING)) || Objects.isNull(monitor)){
                    //??????????????????
                    UserLastWashDto userLastWashDto = (UserLastWashDto) redisTemplate.opsForValue().get(RedisConstants.USER_LAST_WASH+user.getId());
                    if (Objects.nonNull(userLastWashDto)){
                        Duration duration = Duration.between(userLastWashDto.getDateTime(), LocalDateTime.now());
                        userLastWashDto.setTime(Long.valueOf(duration.toMillis()).intValue()/1000);
                        if (Objects.equals(userLastWashDto.getIsUpdateWashTime(),false)) {
                            rocketMQTemplate.syncSend(TopicConstants.UPDATE_WASH_TIME, MessageBuilder.withPayload(jacksonObjectMapper.writeValueAsString(userLastWashDto)).build());
                        }
                        userLastWashDto.setIsUpdateWashTime(true);
                        redisTemplate.opsForValue().set(RedisConstants.USER_LAST_WASH+user.getId(),userLastWashDto, RedisConstants.EXPIRE_TIME, TimeUnit.DAYS);
                    }
                }
                //????????????????????????
                if (Objects.nonNull(monitor)|| Objects.nonNull(star)) {
                    userWashService.userWashEevent(deviceDataDto, monitor, star, tag, user);
                }
                if(Objects.nonNull(deviceDataDto.getButtonId())) { //????????????????????????
                    userButtonService.tagButtonEvent(deviceDataDto,monitor,star,tag,user);
                }
            }
            if (Objects.nonNull(patient) && menu.indexOf("????????????")>-1 ) { //??????????????????
                patientService.patientEvent(deviceDataDto,monitor,star,tag,patient);
            }
            if (Objects.nonNull(temporaryPerson)) { //????????????????????????
                temporaryPersonService.temporaryPersonEvent(deviceDataDto,monitor,star,tag,temporaryPerson);
            }
            if (((Objects.equals(tag.getPurpose(), TagPurpose.THERMOHYGROGRAPH) && menu.indexOf("????????????")>-1)|| (Objects.equals(tag.getPurpose(), TagPurpose.ASSETS)&& menu.indexOf("????????????")>-1))){ //????????????(??????,????????????)??????
                deviceService.deviceEevent(deviceDataDto,monitor,star,tag);
            }
            if (Objects.equals(monitor.getDeviceClassify(),DeviceClassify.RECYCLING_BOX)) {
                recyclingBoxService.event(deviceDataDto,monitor,star,tag,patient,temporaryPerson, user);
            }

            //???????????????
            if (Objects.equals(deviceDataDto.getMonitorBattery(),2) && Objects.nonNull(monitor) ){
                batteryAlarmService.deviceLowBatteryAlarm(monitor,deviceDataDto);
            }
            if (Objects.equals(deviceDataDto.getTagBattery(),2) && Objects.nonNull(tag)){
                if (Objects.nonNull(user)) {
                    batteryAlarmService.userLowBatteryAlarm(user,deviceDataDto,tag);
                }else if (Objects.nonNull(assets) && menu.indexOf("????????????")>-1) {
                    batteryAlarmService.assetsLowBatteryAlarm(assets,deviceDataDto,tag);
                }else if (Objects.nonNull(patient)&& menu.indexOf("????????????")>-1) {
                    batteryAlarmService.patientLowBatteryAlarm(patient,deviceDataDto,tag);
                }else if (Objects.nonNull(temporaryPerson)) {
                    batteryAlarmService.temporaryPersonLowBatteryAlarm(temporaryPerson,deviceDataDto,tag);
                }else if (Objects.equals(tag.getPurpose(),TagPurpose.THERMOHYGROGRAPH) && menu.indexOf("????????????")>-1) {
                    batteryAlarmService.tagLowBatteryAlarm(deviceDataDto,tag);
                }
            }

            updateDeviceBattery(monitor,deviceDataDto.getMonitorBattery());
            updateTagBattery(tag,deviceDataDto.getTagBattery());

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void updateDeviceBattery(Device device,Integer battery){
        if (Objects.nonNull(device)){
            if (!Objects.equals(device.getBattery(),battery)){
                deviceExposeService.updateBattery(device.getId(),battery);
            }
        }
    }

    private void updateTagBattery(Tag tag,Integer battery){
        if (Objects.nonNull(tag)){
            if (!Objects.equals(tag.getBattery(),battery)){
                tagExposeService.updateBattery(tag.getId(),battery);
            }
        }
    }


}
