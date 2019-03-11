package com.codingforhappy.controller;

import com.codingforhappy.model.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.codingforhappy.constants.AccountTypes.DRIVER;

/**
 * 用于测试司机
 */
@Controller
@RequestMapping("/test/driver")
public class TestDriverController extends UserController {
    private static final Logger logger = LoggerFactory.getLogger(TestPassengerController.class);

    /**
     * 司机推送测试
     *
     * @param token
     * @return
     */
    @GetMapping(value = "/sendToTest", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse sendToTest(@RequestBody String token) {
        logger.info("准备推送给用户token={}", token.length() > 10 ? token.substring(0, 10) : token);
        String phoneNumber = getPhoneNumberFromToken(DRIVER, token);
        boolean pushResult = pushService.sendToSubscribedDriver(phoneNumber, "***这是服务器推送给用户的信息***");
        logger.info("推送结果：{}", pushResult);
        if (pushResult)
            return OKResponse();
        else
            return invalidRequestResponse();
    }
}
