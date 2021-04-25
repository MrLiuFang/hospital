package com.lion.event.utils;

import com.lion.event.constant.DelayLevelConstants;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/25 下午5:18
 **/
public class MessageDelayUtil {

    public static Integer getDelayLevel(LocalDateTime dateTime){
        Duration duration = Duration.between(dateTime,LocalDateTime.now());
        long millis = duration.toMinutes();
        Integer delayLevel;
        switch (Long.valueOf(millis).intValue()){
            case DelayLevelConstants.oneMinute :
                delayLevel=DelayLevelConstants.oneMinute;
                break;
            case DelayLevelConstants.twoMinute :
                delayLevel=DelayLevelConstants.twoMinute;
                break;
            case DelayLevelConstants.threeMinute :
                delayLevel=DelayLevelConstants.threeMinute;
                break;
            case DelayLevelConstants.fourMinute :
                delayLevel=DelayLevelConstants.fourMinute;
                break;
            case DelayLevelConstants.fiveMinute :
                delayLevel=DelayLevelConstants.fiveMinute;
                break;
            case DelayLevelConstants.sixMinute :
                delayLevel=DelayLevelConstants.sixMinute;
                break;
            case DelayLevelConstants.sevenMinute :
                delayLevel=DelayLevelConstants.sevenMinute;
                break;
            case DelayLevelConstants.eightMinute :
                delayLevel=DelayLevelConstants.eightMinute;
                break;
            default:
                delayLevel=-1;
        }
        return delayLevel;
    }
}
