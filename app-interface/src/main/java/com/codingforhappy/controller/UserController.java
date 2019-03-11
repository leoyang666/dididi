package com.codingforhappy.controller;

import com.codingforhappy.dao.redis.RedisService;
import com.codingforhappy.login.LoginService;
import com.codingforhappy.model.APIResponse;
import com.codingforhappy.model.APIResponse.Event;
import com.codingforhappy.model.APIResponse.Message;
import com.codingforhappy.model.CheckableUser;
import com.codingforhappy.model.Token;
import com.codingforhappy.service.DriverMybatisService;
import com.codingforhappy.service.PassengerMybatisService;
import com.codingforhappy.service.push.NettyPushService;
import com.codingforhappy.service.register.RegisterService;
import com.codingforhappy.sms.service.SMSResponese;
import com.codingforhappy.token.JwtUtils;
import com.codingforhappy.token.TokenState;
import com.codingforhappy.util.PasswordUtils;
import com.codingforhappy.util.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.codingforhappy.constants.AccountTypes.DRIVER;
import static com.codingforhappy.constants.AccountTypes.PASSENGER;

public abstract class UserController {
    static final Point INVALID_POINT = new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    RedisService redisService;

    @Autowired
    LoginService loginService;

    @Autowired
    DriverMybatisService driverDBService;

    @Autowired
    PassengerMybatisService passengerDBService;

    @Autowired
    NettyPushService pushService;

    RegisterService registerService;

    public void setRegisterService(RegisterService registerService) {
        this.registerService = registerService;
    }

    public APIResponse register(CheckableUser user, String code) {
        // 获取明文密码
        if (!Base64.isBase64(user.getPassword()))
            return new APIResponse(Event.WRONG_FORMAT, Message.WRONG_FORMAT);

        byte[] base64DecodedPassword = Base64.decodeBase64(user.getPassword());
        try {
            PrivateKey privateKey = RSAUtils.getPrivateKey();
            String decryptedPassword = new String(RSAUtils.decrypt(base64DecodedPassword, privateKey));
            user.setPassword(decryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return new APIResponse(Event.WRONG_FORMAT, Message.WRONG_FORMAT);
        }

        // 信息格式验证
        if (!registerService.checkDataFormat(user))
            return new APIResponse(Event.WRONG_FORMAT, Message.WRONG_FORMAT);

        // 验证短信验证码
        SMSResponese smsResponese = registerService.authSMSCode(user.getPhoneNum(), code);
        if (smsResponese.getEvent().equals(SMSResponese.Event.VERIFICATION_FAILED))
            return APIResponse.fromSMSResponse(smsResponese);

        // 密码加盐
        String salted = PasswordUtils.getSaltedPassword(user.getPassword());
        user.setPassword(salted);

        // 尝试存入数据库
        if (registerService.addUser(user))
            return new APIResponse(Event.OK, Message.OK);
        else
            return new APIResponse(Event.USER_HAS_EXISTED, Message.USER_HAS_EXISTED);
    }

    boolean checkLoginRequestFormat(Map<String, Object> request) {
        //检查参数格式
        if (!(request.containsKey("phoneNumber") &&
                request.containsKey("password")))
            return false;

        //检查密码是否Base64编码
        String password = (String) request.get("password");
        if (!Base64.isBase64(password))
            return false;

        return true;
    }

    boolean checkRequestByToken(Map<String, Object> request) {
        if (request.containsKey("token"))
            return true;
        return false;
    }

    boolean checkRequestByPosition(Map<String, Object> request) {
        if (request.containsKey("token") &&
                request.containsKey("position"))
            return true;
        return false;
    }

    boolean checkRequestByOption(Map<String, Object> request) {
        if (request.containsKey("token") && request.containsKey("option"))
            return true;
        return false;
    }

    //下面是一些常用的Response
    //************************************************************
    APIResponse invalidRequestResponse() {
        return new APIResponse(Event.INVALID_REQUEST,
                Message.INVALID_REQUEST);
    }

    APIResponse positionResponse(Point pos) {
        APIResponse response = new APIResponse(Event.OK, Message.OK);
        response.setObj(pos);
        return response;
    }

    /**
     * 手机号和密码成功匹配，返回的token有效期7天
     */
    private APIResponse<Token> matchedResponse(int type, String phoneNumber) {
        APIResponse<Token> response = new APIResponse<>();
        String token = "";

        response.setEvent(Event.OK);
        response.setMsg(Message.OK);
        token = JwtUtils.createToken(new HashMap<String, Object>() {
            {
                put("phoneNumber", phoneNumber);
                put("ext", new Date().getTime() + 1000 * 3600 * 24 * 7);
            }
        });
        //在redis里设置token->phoneNumber
        redisService.putTokenToRedis(type, token, phoneNumber);
        //更新mysql中的token字段
        loginService.updateToken(type, token, phoneNumber);
        response.setObj(new Token(token));

        return response;
    }

    /**
     * 手机号和密码不匹配
     */
    private APIResponse notMatchResponse() {
        return new APIResponse<>(Event.UNAUTHORIZED, Message.NOT_MATCH);
    }

    APIResponse unauthorizedResponse() {
        return  new APIResponse<>(Event.UNAUTHORIZED, Message.UNAUTHORIZED);
    }

    APIResponse OKResponse() {
        return new APIResponse(Event.OK, Message.OK);
    }

    APIResponse notFoundResponse() {
        return new APIResponse(Event.NOT_FOUND, Message.NOT_FOUND);
    }

    /**
     * 未注册推送服务
     */
    APIResponse unsubscribedResponse() {
        return new APIResponse(Event.UNSUBSCRIBED, Message.UNSUBSCRIBED);
    }

    /**
     * 推送失败
     */
    APIResponse pushFailedResponse() {
        return new APIResponse(Event.PUSH_FAILED, Message.PUSH_FAILED);
    }

    /**
     * 目前未实现的功能
     */
    APIResponse notImplementedResponse() {
        return new APIResponse(Event.NOTIMPLEMENTED, Message.NOTIMPLEMENTED);
    }

    //************************************************************
    //上面是一些常用的Response

    /**
     * 用token验证，已经成功登录
     *
     * @param phoneNumber 手机号，用于返回
     * @return 响应
     */
    private APIResponse<String> loginedResponse(String phoneNumber) {
        APIResponse<String> response = new APIResponse<>();
        response.setEvent(Event.OK);
        response.setMsg(Message.OK);
        response.setObj(phoneNumber);
        return response;
    }

    APIResponse<Token> verifiedResponse(int type, boolean match, String phoneNumber) {
        if (match) {
            return matchedResponse(type, phoneNumber);
        } else {
            return notMatchResponse();
        }
    }

    /**
     * 直接使用token登录，有几种情况
     * 0. token 格式无效，返回false
     * 1. token在redis里，直接登录。
     * 2. token不在redis里，
     * db中的token还是和收到的token匹配的，把token放到redis里，返回成功
     * db中的token和收到的token不匹配，返回失败
     *
     * @param type  司机还是乘客
     * @param token token
     * @return 响应，是否成功用token登录
     */
    APIResponse verifyLoginByToken(int type, String token) {
        //检验token是否有效，
        Map<String, Object> tokenMap = JwtUtils.verifyToken(token);
        TokenState state = TokenState.getTokenState((String) tokenMap.get("state"));
        if (state != TokenState.VALID)
            return unauthorizedResponse();

        //去redis里查找token
        String key = redisService.getTokenKey(type, token);
        String phoneNumber = redisService.getPhoneNumber(key);
//        System.out.println("phoneNumber got from redis: " + phoneNumber);
        //如果phoneNumber为空，可能redis里的token过期，或者是token错误
        if (phoneNumber.equals("")) {
            //去db里验证其是否和手机号匹配
            boolean match = loginService.checkTokenFromDB(type, token);
            if (match) {
                //token和db中的匹配，将token放到redis，然后返回成功
                Map<String, Object> data = JwtUtils.getPayload(token);
                phoneNumber = (String) data.get("phoneNumber");
                redisService.putTokenToRedis(type, token, phoneNumber);
                return loginedResponse(phoneNumber);
            } else
                return unauthorizedResponse();
        } else
            return loginedResponse(phoneNumber);
    }

    /**
     * 验证token，有几种情况
     * 0. token 格式无效，返回false
     * 1. token在redis里，返回true。
     * 2. token不在redis里，
     * db中的token还是和收到的token匹配的，把token放到redis里，返回true
     * db中的token和收到的token不匹配，返回false
     */
    private boolean checkToken(int type, String token) {
        //检验token是否有效
        Map<String, Object> tokenMap = JwtUtils.verifyToken(token);
        TokenState state = TokenState.getTokenState((String) tokenMap.get("state"));
        if (state != TokenState.VALID) {
            System.out.println("token检验失败");
            return false;
        }

        //去redis里查找token
        String key = redisService.getTokenKey(type, token);
        String phoneNumber = redisService.getPhoneNumber(key);
        System.out.println("phoneNumber got from redis: " + phoneNumber);
        //如果phoneNumber为空，可能redis里的token过期，或者是token错误
        if (phoneNumber.equals("")) {
            //去db里验证其是否和手机号匹配
            boolean match = loginService.checkTokenFromDB(type, token);
            if (match) {
                //token和db中的匹配，将token放到redis，然后返回成功
                Map<String, Object> data = JwtUtils.getPayload(token);
                phoneNumber = (String) data.get("phoneNumber");
                redisService.putTokenToRedis(type, token, phoneNumber);
                return true;
            } else {
                System.out.println("token和db中的不匹配");
                return false;
            }
        } else
            return true;
    }

    /**
     * 从token中获取手机号，会先验证token合法性
     *
     * @return 如果token合法，返回手机号，否则返回空字符串
     */
    public String getPhoneNumberFromToken(int type, String token) {
        if (!checkToken(type, token))
            return "";
        return (String) JwtUtils.getPayload(token).get("phoneNumber");
    }

    /**
     * 把坐标写入或更新到redis里
     *
     * @param x 经度
     * @param y 纬度
     */
    APIResponse updatePositionByToken(int type, String token, double x, double y) {
        if (!checkToken(type, token))
            return unauthorizedResponse();
        String phoneNumber = (String) JwtUtils.getPhoneNumber(token);
        redisService.addPosition(type, phoneNumber, x, y);
        return OKResponse();
    }

    /**
     * 司机查找当前乘客的手机号，或者乘客查找当前司机的手机号
     *
     * @param type            自己的类型
     * @param selfPhoneNumber 自己的手机号
     * @return 对方的手机号。如果找不到，返回空字符串
     */
    String getCounterpart(int type, String selfPhoneNumber) {
        String CounterpartPhoneNumber = redisService.findCounterpartPhoneNumber(type, selfPhoneNumber);
        if (CounterpartPhoneNumber != null)
            return CounterpartPhoneNumber;
        else {
            System.out.println("in getCounterpart: CounterpartPhoneNumber is null");
            return "";
        }
    }


    APIResponse CounterpartPositionResponse(int type, String token) {
        if (!checkToken(type, token))
            return unauthorizedResponse();

        String selfPhoneNumber = (String) JwtUtils.getPayload(token).get("phoneNumber");
        String counterpartPhoneNumber = getCounterpart(type, selfPhoneNumber);
        System.out.println("counterpartPhoneNumber: " + counterpartPhoneNumber);

        if (counterpartPhoneNumber.isEmpty())
            return new APIResponse(Event.NOT_FOUND,
                    Message.COUNTERPART_NOT_FOUND);

        Point pos;
        if (type == PASSENGER)
            pos = redisService.getPosition(DRIVER, counterpartPhoneNumber);
        else //if(type == DRIVER)
            pos = redisService.getPosition(PASSENGER, counterpartPhoneNumber);

        if (pos == null)
            return new APIResponse(Event.NOT_FOUND,
                    Message.POSITION_NOT_FOUND);

        return positionResponse(pos);
    }

    /**
     * 检查位置的合法性，这是redis的限制
     *
     * @param x 经度
     * @param y 纬度
     */
    private boolean checkPosition(double x, double y) {
        return !(x > 180.0 || x < -180.0 || y > 85.05112878 || y < -85.05112878);
    }

    /**
     * 从request中获取坐标信息
     * @param request { "token": "xxx",
     *                "position": { "x": 1.1, "y": 33 }
     *                }
     * @return 如果请求错误，返回无效的Point, Point(-1, -1)
     */
    Point getPointFromPositionRequest(Map<String, Object> request) {

        if (!checkRequestByPosition(request)) {
            return INVALID_POINT;
        }
        //String token = (String) request.get("token");
        //获取请求中的位置
        Map<String, Double> position;
        try {
            position = (Map<String, Double>) request.get("position");
        } catch (Exception e) {
            System.err.println("请求中的position错误");
            return INVALID_POINT;
        }
        double x = position.get("x");
        double y = position.get("y");

        return new Point(x, y);
    }


}
