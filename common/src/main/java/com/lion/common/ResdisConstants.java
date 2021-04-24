package com.lion.common;

import javax.print.attribute.standard.PrinterURI;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 下午4:35
 **/
public class ResdisConstants {

    /**
     * 用户(key:userId,value:user)
     */
    public static String USER = "user_";

    /**
     * 洗手规则(key:washId,value:wash)
     */
    public static String WASH = "wash_";

    /**
     * 区域洗手规则(key:regionId,value:List<wash>)
     */
    public static String REGION_WASH = "region_wash_";

    /**
     * 区域用户洗手规则(key:regionId+userId,value:wash)
     */
    public static String REGION_USER_WASH = "region_user_wash_";

    /**
     * 警报规则(key:alarmId,value:alarm)
     */
    public static String ALARM = "alarm_";

    /**
     * 设备归属哪个区域(key:deviceId,value:region)
     */
    public static String DEVICE_REGION = "device_region_";

    /**
     * 区域公开对象(key:regionId,value:List<region>)
     */
    public static String REGION_EXPOSE_OBJECT = "region_expose_object_";

    /**
     * 设备(key:deviceId,value:device)
     */
    public static String DEVICE = "device_";

    /**
     * 设备(key:deviceCode,value:device)
     */
    public static String DEVICE_CODE = "device_code";

    /**
     * 标签(key:tagId,value:tag)
     */
    public static String TAG = "tag_";

    /**
     * 标签(key:tagCode,value:tag)
     */
    public static String TAG_CODE = "tag_code_";

    /**
     * 用户与的标签关联(key:userId,value:tag)
     */
    public static String USER_TAG = "user_tag_";

    /**
     * 用户与的标签关联(key:tagId,value:user)
     */
    public static String TAG_USER = "tag_user_";

    /**
     * 用户最后的洗手(key:userId,value:lastUserWashDto)
     */
    public static String USER_LAST_WASH = "user_last_wash_";
}
