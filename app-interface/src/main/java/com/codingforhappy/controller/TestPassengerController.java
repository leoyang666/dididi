package com.codingforhappy.controller;

import com.codingforhappy.model.APIResponse;
import com.codingforhappy.model.NewTripInfo;
import com.codingforhappy.model.NewTripOrder;
import com.codingforhappy.model.Point;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * 用于测试乘客
 */
@Controller
@RequestMapping("/test/passenger")
public class TestPassengerController extends UserController {
    private static final Logger logger = LoggerFactory.getLogger(TestDriverController.class);

    /**
     * 测试NewTripOrder类的序列化
     */
    @GetMapping(value = "/NewTripOrder", produces = "application/json;charset=utf-8")
    @ResponseBody
    public NewTripOrder getNewTripOrder() {
        NewTripOrder newTripOrder = new NewTripOrder();
        newTripOrder.setToken("xxx");
        newTripOrder.setStartingDesc("宁波");
        newTripOrder.setDestDesc("杭州");
        Random random = new Random();
        newTripOrder.setDestPos(new Point(random.nextDouble(), random.nextDouble()));
        newTripOrder.setStartingPos(new Point(random.nextDouble(), random.nextDouble()));
        return newTripOrder;
    }

    /**
     * 测试NewTripOrder类的反序列化
     */
    @PostMapping(value = "/receiveNewTripOrder", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse newtriptest(@RequestBody NewTripOrder order) {
        return OKResponse();
    }

    @PostMapping(value = "/seeLoggerInfo", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse newTripInfoTest() {
        NewTripInfo tripInfo = new NewTripInfo(getNewTripOrder());
        ObjectMapper Obj = new ObjectMapper();
        String temp = "";
        try {
            temp = Obj.writerWithDefaultPrettyPrinter().writeValueAsString(tripInfo);
            logger.info("NewTripInfo 2 String\n{}", temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        APIResponse response = new APIResponse(APIResponse.Event.OK, APIResponse.Message.OK);
        response.setObj(tripInfo);
        return response;
    }
}
