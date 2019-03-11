package com.codingforhappy.service;

import com.codingforhappy.dao.sql.PassengersDao;
import com.codingforhappy.model.PassengerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassengerMybatisService {
    private static Logger logger = LoggerFactory.getLogger(DriverMybatisService.class);

    private PassengersDao passengersDao;

    @Autowired
    public PassengerMybatisService(PassengersDao passengersDao) {
        this.passengersDao = passengersDao;
    }

    /**
     * 用司机手机号获取司机信息
     *
     * @param phoneNumber 司机手机号
     * @return DriverInfo类型
     */
    public PassengerInfo getInfoByPhoneNumber(String phoneNumber) {
        return passengersDao.getInfoByPhoneNumber(phoneNumber);
    }
}
