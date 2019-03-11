package com.codingforhappy.login;

import com.codingforhappy.token.JwtUtils;
import com.codingforhappy.util.PasswordUtils;
import com.codingforhappy.util.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Base64;
import java.util.Map;

import static com.codingforhappy.constants.AccountTypes.DRIVER;
import static com.codingforhappy.constants.AccountTypes.PASSENGER;

@Service
public class LoginService {
    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    private LoginMybatisService mybatisService;

    @Autowired
    public LoginService(LoginMybatisService mybatisService) {
        this.mybatisService = mybatisService;
    }

    public static void main(String[] args) {
//        LoginService loginService = new LoginService();
//        boolean verifyResult = loginService.verifyPassengerLogin(PASSENGER, "13012310001", "66666");
//        logger.info("verify result: " + verifyResult);
    }

    /**
     * @param type           用户类型，司机还是乘客
     * @param phoneNumber
     * @param Base64password
     * @return 手机号和密码是否和数据库中的匹配
     */
    public boolean verifyLogin(int type, String phoneNumber, String Base64password) {
        byte[] decodedBase64 = new byte[]{};

        //TODO 验证参数格式，防止SQL注入
        //如果收到的密码不是base64编码格式，会抛出异常
        try {
            decodedBase64 = Base64.getDecoder().decode(Base64password);
        } catch (IllegalArgumentException e) {
            return false;
        }

        //获取明文密码
        PrivateKey privateKey = null;
        String decryptedPassword = "";
        try {
            privateKey = RSAUtils.getPrivateKey();
            decryptedPassword = new String(RSAUtils.decrypt(decodedBase64, privateKey));
        } catch (Exception e) {
            logger.error("decrypt error!!");
            e.printStackTrace();
        }

        //检查账号密码和数据库中的是否匹配
        String salted = PasswordUtils.getSaltedPassword(decryptedPassword);

        boolean verified = false;
        if (type == PASSENGER) {
            verified = mybatisService.verifyPassengerLoginByPassword(phoneNumber, salted);
        } else if (type == DRIVER) {
            verified = mybatisService.verifyDriverLoginByPassword(phoneNumber, salted);
        }

        if (verified) {
//            dao.updateLastLoginTime(phoneNumber);
            return true;
        } else
            return false;
    }

    public void updateToken(int type, String token, String phoneNumber) {
        if (type == PASSENGER)
            mybatisService.updatePassengerToken(token, phoneNumber);
        else if (type == DRIVER) {
            mybatisService.updateDriverToken(token, phoneNumber);
        }
    }

    /**
     * 先提取出token里的手机号，然后用手机号查找数据库中此手机号的token，看是否匹配。
     *
     * @param token token
     * @return 是否匹配
     */
    public boolean checkTokenFromDB(int type, String token) {
        //data={"ext":1586670861792,"phoneNumber":"13012310000"}
        Map<String, Object> data = JwtUtils.getPayload(token);
        String phoneInToken = (String) data.get("phoneNumber");
        if (type == PASSENGER) {
            String foundToken = mybatisService.getPassengerTokenByPhoneNumber(phoneInToken);
            if (foundToken == null) return false;
            System.out.println("**found passenger token:" + foundToken);
            return foundToken.equals(token);
        } else if (type == DRIVER) {
            String foundToken = mybatisService.getDriverTokenByPhoneNumber(phoneInToken);
            if (foundToken == null) return false;
            System.out.println("**found driver token:" + foundToken);
            return foundToken.equals(token);
        } else
            return false;
    }
}
