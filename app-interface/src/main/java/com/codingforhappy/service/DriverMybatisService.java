package com.codingforhappy.service;

import com.codingforhappy.dao.sql.DriversDao;
import com.codingforhappy.model.DriverInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverMybatisService {
    private static Logger logger = LoggerFactory.getLogger(DriverMybatisService.class);

    private DriversDao driversDao;

    @Autowired
    public DriverMybatisService(DriversDao driversDao) {
        this.driversDao = driversDao;
    }

    /**
     * 用司机手机号获取司机信息
     *
     * @param phoneNumber 司机手机号
     * @return DriverInfo类型
     */
    public DriverInfo getInfoByPhoneNumber(String phoneNumber) {
        return driversDao.getInfoByPhoneNumber(phoneNumber);
    }


}
