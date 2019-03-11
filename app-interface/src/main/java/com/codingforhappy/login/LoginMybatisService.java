package com.codingforhappy.login;

import com.codingforhappy.dao.sql.DriversDao;
import com.codingforhappy.dao.sql.PassengersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginMybatisService {
    private static Logger logger = LoggerFactory.getLogger(LoginMybatisService.class);

    private PassengersDao passengersDao;
    private DriversDao driversDao;

    @Autowired
    public LoginMybatisService(PassengersDao passengersDao, DriversDao driversDao) {
        this.passengersDao = passengersDao;
        this.driversDao = driversDao;
    }

    /**
     * @param phoneNumber    手机号
     * @param saltedPassword 加盐后的密码，和数据库中的一样
     * @return true = 匹配, false = 不匹配
     */
    public boolean verifyPassengerLoginByPassword(String phoneNumber, String saltedPassword) {
        Map<String, String> paramater = new HashMap<>();
        paramater.put("phonenum", phoneNumber);
        paramater.put("password", saltedPassword);

        int result = passengersDao.verifyLoginByPassword(paramater);
        return result != 0;
    }

    /**
     * 更新mysql中用户的token字段
     *
     * @param token       token
     * @param phoneNumber 手机号
     */
    public void updatePassengerToken(String token, String phoneNumber) {
        Map<String, String> parameter = new HashMap<String, String>() {
            {
                put("phonenum", phoneNumber);
                put("token", token);
            }
        };

        passengersDao.updateToken(parameter);
    }

    public String getPassengerTokenByPhoneNumber(String phoneNumber) {
        return passengersDao.getTokenByPhoneNumber(phoneNumber);
    }

    public boolean verifyDriverLoginByPassword(String phoneNumber, String saltedPassword) {
        Map<String, String> paramater = new HashMap<>();
        paramater.put("phonenum", phoneNumber);
        paramater.put("password", saltedPassword);

        int result = driversDao.verifyLoginByPassword(paramater);
        return result != 0;
    }

    public void updateDriverToken(String token, String phoneNumber) {
        Map<String, String> parameter = new HashMap<String, String>() {
            {
                put("phonenum", phoneNumber);
                put("token", token);
            }
        };

        driversDao.updateToken(parameter);
    }

    public String getDriverTokenByPhoneNumber(String phoneNumber) {
        return driversDao.getTokenByPhoneNumber(phoneNumber);
    }


//    public boolean checkPassengerUnique(String phoneNumber) {
//        int count = passengersDao.countByPhoneNumber(phoneNumber);
//
//
//        return count == 0;
//    }

//    public void updateLastLoginTime(String phoneNumber) {
//        SqlSession session = LoginMybatisService.getPassengerSession();
//        if(session == null)
//            return;
//
//        session.update("Passenger.updateLastLoginTime", phoneNumber);
//        session.commit();
//        session.close();
//    }

}
