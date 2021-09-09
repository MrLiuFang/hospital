package com.lion.common.utils;

/**
 * @Author Mr.Liu
 * @Description
 * @Date 2021/5/3 下午4:57
 **/
public class DateTimeFormatterUtil  {

    public static String pattern(String dateTime) {
        if (dateTime.indexOf("T")>-1 && dateTime.indexOf("Z")>-1) {
            return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        }else if (dateTime.indexOf("T")>-1 && dateTime.lastIndexOf(".")>-1){
            if (dateTime.substring(dateTime.lastIndexOf(".")+1).length()==1){
                return "yyyy-MM-dd'T'HH:mm:ss.S";
            }else if (dateTime.substring(dateTime.lastIndexOf(".")+1).length()==2){
                return "yyyy-MM-dd'T'HH:mm:ss.SS";
            }else if (dateTime.substring(dateTime.lastIndexOf(".")+1).length()==3){
                return "yyyy-MM-dd'T'HH:mm:ss.SSS";
            }
        }else if (dateTime.indexOf("T")>-1){
            return "yyyy-MM-dd'T'HH:mm:ss";
        } else if (dateTime.indexOf(" ")>-1){
            return "yyyy-MM-dd HH:mm:ss";
        }
        return "yyyy-MM-dd";
    }
}
