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
        if (millis<1000*60 && millis>0){
           return DelayLevelConstants.oneMinute;
        }
        Integer minutes = Long.valueOf(millis).intValue()/(1000*60);
        if ((Long.valueOf(millis).intValue()%(1000*60))>0){
            minutes = minutes+1;
        }
        Integer delayLevel;
        if (minutes>8){
            return DelayLevelConstants.eightMinute;
        }
        switch (minutes){
            case 1 :
                delayLevel=DelayLevelConstants.oneMinute;
                break;
            case 2 :
                delayLevel=DelayLevelConstants.twoMinute;
                break;
            case 3 :
                delayLevel=DelayLevelConstants.threeMinute;
                break;
            case 4 :
                delayLevel=DelayLevelConstants.fourMinute;
                break;
            case 5 :
                delayLevel=DelayLevelConstants.fiveMinute;
                break;
            case 6 :
                delayLevel=DelayLevelConstants.sixMinute;
                break;
            case 7 :
                delayLevel=DelayLevelConstants.sevenMinute;
                break;
            case 8 :
                delayLevel=DelayLevelConstants.eightMinute;
                break;
            default:
                delayLevel=-1;
        }
        return delayLevel;
    }
}
