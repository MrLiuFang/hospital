package com.lion.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lion.common.dto.UserLastWashDto;
import com.lion.event.entity.SystemAlarm;

public interface SaveCctvService {

    void saveCctv(UserLastWashDto userLastWashDto) throws JsonProcessingException;

    void saveCctv(SystemAlarm systemAlarm) throws JsonProcessingException;

    String saveWashEventCctv(String id) throws JsonProcessingException;

    String saveAlarmCctv(String id) throws JsonProcessingException;
}
