package com.lion.event.service;

import com.lion.common.dto.UserLastWashDto;
import com.lion.event.entity.SystemAlarm;

public interface SaveCctvService {

    void saveCctv(UserLastWashDto userLastWashDto);

    void saveCctv(SystemAlarm systemAlarm);
}
