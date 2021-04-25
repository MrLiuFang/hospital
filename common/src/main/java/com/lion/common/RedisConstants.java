package com.lion.common;

import javax.print.attribute.standard.PrinterURI;

/**
 * @Author Mr.Liu
 * @Description //TODO
 * @Date 2021/4/23 下午4:35
 **/
public class RedisConstants {

    /**
     * 用户(key:userId,value:user)
     */
    public static final String USER = "user_";

    /**
     * 洗手规则(key:washId,value:wash)
     */
    public static final String WASH = "wash_";

    /**
     * 洗手规则设备类型(key:washId,value:list<WashDeviceType>)
     */
    public static final String WASH_DEVICE_TYPE = "wash_device_type_";

    /**
     * 区域洗手规则(key:regionId,value:List<washId>)
     */
    public static final String REGION_WASH = "region_wash_";

    /**
     * 区域用户洗手规则(key:regionId+userId,value:washId)
     */
    public static final String REGION_USER_WASH = "region_user_wash_";

    /**
     * 用户定时洗手规则(key:userId,value:List<washId>)
     */
    public static final String USER_LOOP_WASH = "user_loop_wash_";

    /**
     * 所有用户定时洗手规则(key:,value:List<washId>)
     */
    public static final String ALL_USER_LOOP_WASH = "all_user_loop_wash_";

    /**
     * 警报规则(key:alarmId,value:alarm)
     */
    public static final String ALARM = "alarm_";

    /**
     * 设备归属哪个区域(key:deviceId,value:regionId)
     */
    public static final String DEVICE_REGION = "device_region_";

    /**
     * 区域公开对象(key:regionId,value:List<ExposeObject>)
     */
    public static final String REGION_EXPOSE_OBJECT = "region_expose_object_";

    /**
     * 区域(key:regionId,value:region)
     */
    public static final String REGION = "region_";

    /**
     * 设备(key:deviceId,value:device)
     */
    public static final String DEVICE = "device_";

    /**
     * 设备(key:deviceCode,value:device)
     */
    public static final String DEVICE_CODE = "device_code";

    /**
     * 标签(key:tagId,value:tag)
     */
    public static final String TAG = "tag_";

    /**
     * 标签(key:tagCode,value:tag)
     */
    public static final String TAG_CODE = "tag_code_";

    /**
     * 用户与的标签关联(key:userId,value:tagId)
     */
    public static final String USER_TAG = "user_tag_";

    /**
     * 用户与的标签关联(key:tagId,value:userId)
     */
    public static final String TAG_USER = "tag_user_";

    /**
     * 用户最后的洗手(key:userId,value:lastUserWashDto)
     */
    public static final String USER_LAST_WASH = "user_last_wash_";

    /**
     * 当前用户所在的区域(key:userId,value:region)
     */
    public static final String USER_CURRENT_REGION = "user_current_region_";


    /**
     * 缓存过期时间
     */
    public static final Integer EXPIRE_TIME = 364;
}
