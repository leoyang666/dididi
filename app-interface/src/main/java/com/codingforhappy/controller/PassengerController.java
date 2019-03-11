package com.codingforhappy.controller;

import com.codingforhappy.model.*;
import com.codingforhappy.model.APIResponse.Event;
import com.codingforhappy.model.APIResponse.Message;
import com.codingforhappy.service.register.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.codingforhappy.constants.AccountTypes.PASSENGER;

@Controller
@RequestMapping("/passenger")
public class PassengerController extends UserController {
    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    @Override
    @Autowired
    @Qualifier("passengerRegisterService")
    public void setRegisterService(RegisterService registerService) {
        super.setRegisterService(registerService);
    }

    @PostMapping(produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse requestRegistration(Passenger passenger, @RequestParam("code") String code) {
        return register(passenger, code);
    }

    /**
     * @param request: key需要包含 "phoneNumber", "password"
     */
    @GetMapping(value = "/token", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse verifyPassengerLogin(@RequestBody Map<String, Object> request) {
        //检查request完整性
        if (!checkLoginRequestFormat(request)) {
            return invalidRequestResponse();
        }

        String phoneNumber = (String) request.get("phoneNumber");
        String password = (String) request.get("password");

        boolean match = loginService.verifyLogin(PASSENGER, phoneNumber, password);

        return verifiedResponse(PASSENGER, match, phoneNumber);

    }

    @GetMapping(value = "/verification", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse verifyPassengerToken(@RequestBody Map<String, Object> request) {
        if (!request.containsKey("token")) {
            return invalidRequestResponse();
        }
        String token = (String) request.get("token");
        return verifyLoginByToken(PASSENGER, token);
    }

    /**
     * 获取司机当前位置
     *
     * @param request 包含token
     * @return
     */
    @GetMapping(value = "/driver_position", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse getDriverPosition(@RequestBody Map<String, Object> request) {
        if (!checkRequestByToken(request)) {
            return invalidRequestResponse();
        }
        String token = (String) request.get("token");
        return CounterpartPositionResponse(PASSENGER, token);

    }


    /**
     * 乘客开始打车，请求新行程
     *
     * @param order NewTripOrder类型包含乘客token、出发地信息、目的地信息
     * @return 成功匹配到司机后，返回给乘客新行程的信息，但是司机可能取消订单
     */
    @GetMapping(value = "/newTripInfo", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse tripRequest(@RequestBody NewTripOrder order) {
        String token = order.getToken();
        String passengerPhoneNumber = getPhoneNumberFromToken(PASSENGER, token);
        //检查token和获取乘客手机号
        if (passengerPhoneNumber.isEmpty())
            return unauthorizedResponse();
        //获取附近可以匹配的司机
        String driverPhoneNumber = redisService.findAvailableDriver(order, passengerPhoneNumber);
        if (driverPhoneNumber.isEmpty())
            return notFoundResponse();

        APIResponse response = new APIResponse(Event.OK, Message.OK);
        //设置需要返回的新行程信息
        NewTripInfo tripInfo = new NewTripInfo(order);
        DriverInfo driverInfo = driverDBService.getInfoByPhoneNumber(driverPhoneNumber);
        tripInfo.setDriver(driverInfo);
        PassengerInfo passengerInfo = passengerDBService.getInfoByPhoneNumber(passengerPhoneNumber);
        tripInfo.setPassenger(passengerInfo);
        tripInfo.setStartTime(System.currentTimeMillis());

        logger.info("trip info = {}", tripInfo);

        //把行程推送给司机，并获得推送结果
        //todo 推送失败的情况未处理
        boolean pushSucceed = pushService.sendToSubscribedDriver(driverPhoneNumber, tripInfo.toString());

        redisService.storeNewTripInfo(tripInfo, passengerPhoneNumber, driverPhoneNumber);
        //把NewTripInfo放到response里
        response.setObj(tripInfo);

        return response;
    }

    /**
     * 乘客确认上车
     * 前置：乘客收到司机的确认信息推送，司机订阅了推送
     * todo 乘客可以取消上车，需要推送给司机乘客已取消，目前未做任何处理
     *
     * @param request 确认或取消
     *                {"token": "xxx", "option": "YES" or "NO" }
     * @return
     */
    @PostMapping(value = "/aboardAck", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse aboard(@RequestBody Map<String, Object> request) {
        if (!checkRequestByOption(request)) {
            return invalidRequestResponse();
        }
        String token = (String) request.get("token");
        String passengerPhoneNumber = (String) getPhoneNumberFromToken(PASSENGER, token);

        String driverPhoneNumber = getCounterpart(PASSENGER, passengerPhoneNumber);
        if (driverPhoneNumber.isEmpty())
            return notFoundResponse();

        String option = (String) request.get("option");

        if (option.equals("NO")) {
            redisService.clearNewTripInfo(passengerPhoneNumber, driverPhoneNumber);
            return notImplementedResponse();
        }

        String aboardAcknowledged = "乘客已上车";
        boolean pushSucceed = pushService.sendToSubscribedDriver(driverPhoneNumber, aboardAcknowledged);

        if (pushSucceed) {
            return OKResponse();
        } else
            return pushFailedResponse();
    }


}


