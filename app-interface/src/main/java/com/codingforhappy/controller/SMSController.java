package com.codingforhappy.controller;

import com.codingforhappy.dao.sql.ExampleDao;
import com.codingforhappy.model.APIResponse;
import com.codingforhappy.sms.service.SMSResponese;
import com.codingforhappy.sms.service.ShortMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sms")
public class SMSController {

    private ShortMessageService sms;

    private ExampleDao dao;

    @Autowired
    public SMSController(ShortMessageService sms, ExampleDao dao) {
        this.sms = sms;
        this.dao = dao;
    }

    @RequestMapping(value = "/registercode/{phoneNum}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public APIResponse sendRegisterVerificationCode(@PathVariable("phoneNum") String phoneNum) {
        SMSResponese response = sms.sendVerificationCode(phoneNum, ShortMessageService.VerificationCodeType.REGISTER);
        return APIResponse.fromSMSResponse(response);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public APIResponse testauth(@RequestParam("phoneNum") String phoneNum, @RequestParam("code") String code) {
        return APIResponse.fromSMSResponse(sms.authenticateVerificationCode(phoneNum, code, ShortMessageService.VerificationCodeType.REGISTER));
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public APIResponse example() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("phonenum", "17816876192");
        map.put("password", "fad");
        map.put("nickname", "happy");
        dao.insertPassengers(map);
        return new APIResponse("1", "1");
    }
}
