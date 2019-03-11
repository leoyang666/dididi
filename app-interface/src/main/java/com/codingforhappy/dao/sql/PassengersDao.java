package com.codingforhappy.dao.sql;

import com.codingforhappy.model.PassengerInfo;

import java.util.Map;

public interface PassengersDao {
    /**
     * @param map "phonenum" and "password" key is required
     * @return 1 = verified, 0 = not verified
     */
    int verifyLoginByPassword(Map map);

    /**
     * @param map "token" and "phonenum" key is required
     */
    void updateToken(Map map);

    String getTokenByPhoneNumber(String phonenum);

//    int countByPhoneNumber(String phoneNumber);
//    void updateLastLoginTime(String phoneNumber);

    PassengerInfo getInfoByPhoneNumber(String phoneNumber);
}
