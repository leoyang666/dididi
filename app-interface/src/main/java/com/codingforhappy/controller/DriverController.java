package com.codingforhappy.controller;

import com.codingforhappy.dao.PricingMeter;
import com.codingforhappy.model.APIResponse;
import com.codingforhappy.model.Driver;
import com.codingforhappy.model.FinalOrder;
import com.codingforhappy.model.NewTripInfo;
import com.codingforhappy.service.register.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.codingforhappy.constants.AccountTypes.DRIVER;

@Controller
@RequestMapping("/driver")
public class DriverController extends UserController {
    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);

    @Override
    @Autowired
    @Qualifier("driverRegisterService")
    public void setRegisterService(RegisterService registerService) {
        super.setRegisterService(registerService);
    }

    @PostMapping(produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse requestRegistration(Driver driver, @RequestParam("code") String code) {
        return register(driver, code);
    }

    /**
     * 用司机手机号和密码登录，如果成功返回token
     *
     * @param request 手机号和密码
     * @return 登录成功返回token，登录失败返回失败响应
     */
    @GetMapping(value = "/token", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse verifyDriverLogin(@RequestBody Map<String, Object> request) {
        //检查request完整性
        if (!checkLoginRequestFormat(request)) {
            return invalidRequestResponse();
        }

        String phoneNumber = (String) request.get("phoneNumber");
        String password = (String) request.get("password");


        boolean match = loginService.verifyLogin(DRIVER, phoneNumber, password);

        return verifiedResponse(DRIVER, match, phoneNumber);

    }

    /**
     * 验证司机的token
     *
     * @param request token
     * @return 响应，是否成功用token登录
     */
    @GetMapping(value = "/verification", produces = "application/json;charset=utf-8;")
    @ResponseBody
    public APIResponse verifyDriverToken(@RequestBody Map<String, Object> request) {
        if (!checkRequestByToken(request)) {
            return invalidRequestResponse();
        }
        String token = (String) request.get("token");
        return verifyLoginByToken(DRIVER, token);
    }

    /**
     * 上传司机的当前位置
     * { "token": "xxx",
     * "position": { "x": 1.1, "y": 33.0 }
     * }
     * //todo 现在还有一个问题就是没开启自动接单时，上传司机位置也会加入autoAck，可以考虑把autoAck的Set放在内存里，
     * //把司机位置放redis，每次先在内存里查一下是否开启了自动接单。
     * @param request 司机当前位置
     */
    @PostMapping(value = "/position", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse postDriverPosition(@RequestBody Map<String, Object> request) {

        Point point = getPointFromPositionRequest(request);

        if (point.equals(INVALID_POINT))
            return invalidRequestResponse();

        String token = (String) request.get("token");
        return updatePositionByToken(DRIVER, token, point.getX(), point.getY());
    }

    /**
     * 开启自动接单，在这个请求前必须开启推送服务
     *
     * @param request { "token": "xxx",
     *                "position": { "x": 1.1, "y": 33 }
     *                }
     */
    @PostMapping(value = "/autoAckOn", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse autoAckOn(@RequestBody Map<String, Object> request) {
        //获取请求中的位置
        Point point = getPointFromPositionRequest(request);

        //请求错误
        if (point.equals(INVALID_POINT))
            return invalidRequestResponse();

        //根据token获取司机手机号
        String token = (String) request.get("token");
        String phoneNumber = getPhoneNumberFromToken(DRIVER, token);
        if (phoneNumber.isEmpty())
            return unauthorizedResponse();

        //检查司机是否订阅了推送服务
        if (!pushService.checkDriverSubscribed(token))
            return unsubscribedResponse();


        //在redis的GeoSet里添加司机
        redisService.driverAutoAckOn(phoneNumber, point.getX(), point.getY());

        return OKResponse();

    }

    /**
     * 关闭自动接单
     *
     * @param request token
     */
    @PostMapping(value = "/autoAckOff", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse autoAckOff(@RequestBody Map<String, Object> request) {
        if (!checkRequestByToken(request)) {
            return invalidRequestResponse();
        }
        String token = (String) request.get("token");
        String phoneNumber = getPhoneNumberFromToken(DRIVER, token);
        if (phoneNumber.isEmpty())
            return unauthorizedResponse();

        //关闭司机的自动接单,在redis的GeoSet里删除司机
        autoAckOff(token);

        //取消订阅推送服务
        pushService.driverUnsubscribe(token);

        return OKResponse();
    }


    /**
     * 司机确认之前推送的新行程，推送给乘客司机信息
     * 前置：司机和乘客已经建立匹配关系，乘客已经订阅推送
     * todo 司机也可以取消，目前未做任何处理
     *
     * @param request { "token": "xxx", "option": "YES" or "NO"}
     * @return
     */
    @PostMapping(value = "/newTripAck", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse newTripAck(@RequestBody Map<String, Object> request) {
        if (!checkRequestByOption(request)) {
            return invalidRequestResponse();
        }
        String option = (String) request.get("option");
        if (option.equals("NO"))
            return notImplementedResponse();

        String token = (String) request.get("token");
        String driverPhoneNumber = getPhoneNumberFromToken(DRIVER, token);
        //获取已经匹配的乘客的手机号
        String passengerPhoneNumber = getCounterpart(DRIVER, driverPhoneNumber);
        //找不到匹配的乘客
        if (passengerPhoneNumber.isEmpty())
            return notFoundResponse();

        //关闭司机的自动接单
        autoAckOff(token);

        //构造用于推送的确认信息
        String acknowledge = "司机已确认";
        boolean pushSucceed = pushService.sendToSubscribedPassenger(passengerPhoneNumber, acknowledge);
        if (pushSucceed)
            return OKResponse();
        else
            return pushFailedResponse();
    }

    /**
     * 司机手动关闭接单，或者司机确认接单后，需要调用这个方法，关闭自动接单
     */
    private void autoAckOff(String token) {
        String phoneNumber = getPhoneNumberFromToken(DRIVER, token);

        //在redis的GeoSet里删除司机
        redisService.driverAutoAckOff(phoneNumber);
    }

    /**
     * 司机请求结束行程
     *
     * @param request token，结束时司机所在位置，里程
     *                { "token": "xxx",
     *                "position": { "x": 1.1, "y": 33.0 },
     *                "distance": 15.12
     *                }
     *                todo 结束坐标未判断
     * @return 参考车费
     */
    @PostMapping(value = "/endTripRequest", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse endTrip(@RequestBody Map<String, Object> request) {
        //获取请求中的位置
        Point point = getPointFromPositionRequest(request);

        //请求错误
        if (point.equals(INVALID_POINT) || !request.containsKey("distance"))
            return invalidRequestResponse();

        double distance = (double) request.get("distance");
        double price = 0.0;
        try {
            price = PricingMeter.calculatePrice(distance);
        } catch (Exception e) {
            return invalidRequestResponse();
        }
        //将价格进行舍入，保留2位小数，用"银行家舍入"方法
        BigDecimal bd = new BigDecimal(price);
        String roundedPrice = String.valueOf(bd.setScale(2, RoundingMode.HALF_EVEN));
        return OKResponse().setObj(roundedPrice);
    }

    /**
     * 司机确认车费，结束行程。
     * 会返回给司机FinalOrder，推送给乘客FinalOrder
     * @param request token，车费
     *                { "token": "xxx",
     *                "price": "45.50"
     *                }
     * @return
     */
    @PostMapping(value = "/endTripAck", produces = "application/json;charset=utf-8")
    @ResponseBody
    public APIResponse endTripAck(@RequestBody Map<String, Object> request) {
        if (!checkRequestByToken(request) || !request.containsKey("price"))
            return invalidRequestResponse();

        String price = (String) request.get("price");


        String token = (String) request.get("token");
        String driverPhoneNumber = getPhoneNumberFromToken(DRIVER, token);
        String passengerPhoneNumber = getCounterpart(DRIVER, driverPhoneNumber);
        //获得行程信息
        NewTripInfo tripInfo;
        try {
            tripInfo = redisService.getNewTripInfo(DRIVER, driverPhoneNumber);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return notFoundResponse().setMsg("can't find trip information");
        }
        //生成最终订单
        FinalOrder finalOrder = new FinalOrder(tripInfo).setPrice(price);
        logger.info("Final Order = {}", finalOrder);
        boolean pushSucceed = pushService.sendToSubscribedPassenger(passengerPhoneNumber, finalOrder.toString());
        //todo 删除redis里的pair，在数据库里添加本次行程的信息
        if (pushSucceed)
            return OKResponse().setObj(finalOrder);
        else
            return pushFailedResponse();
    }

}
