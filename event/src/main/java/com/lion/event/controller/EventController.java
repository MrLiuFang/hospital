package com.lion.event.controller;

import com.lion.event.entity.Event;
import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Mr.Liu
 * @Description:
 * @date 2021/4/14上午9:33
 */
@RestController
@RequestMapping("")
@Validated
@Api(tags = {"事件"})
public class EventController {

    @PostMapping("/new")
    public String newEvent(@RequestBody Map<String,String> map){
        System.out.println(map);
        return "0";
    }
}
