package com.lion.common.utils;

import com.lion.common.constants.DelayLevelConstants;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/4/25 下午5:18
 **/
public class MessageDelayUtil {

    public static Integer getDelayLevel(LocalDateTime dateTime){
        Duration duration = Duration.between(LocalDateTime.now(),dateTime);
        long millis = duration.toMillis();
        if (millis<=1000*10 && millis>0) {
            Integer second = Long.valueOf(millis).intValue()/(1000);
            return second;
        }else {
            return DelayLevelConstants.tenSecond;
        }
    }
}
