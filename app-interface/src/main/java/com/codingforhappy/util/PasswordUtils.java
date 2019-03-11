package com.codingforhappy.util;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtils {

    /**
     * sha256(sha356($password) + salt)
     *
     * @param password 明文密码
     * @return 插入到数据库中的密码
     */
    public static String getSaltedPassword(String password) {
        String salt = "";
        String sha256ed = DigestUtils.sha256Hex(password);
        String secure = DigestUtils.sha256Hex(sha256ed + salt);
        return secure;
    }

    public static void main(String[] args) {
        System.out.println(getSaltedPassword("123456"));
        System.out.println(getSaltedPassword("666666"));
//        getSaltedPassword("123456");
    }

    //123456
    //aae4b5f627b7b1e407622c21710c47bf82793d9034b8d53e7b14a66d496b1e10
    //666666
    //6064273df4a8c91be1a561bd78c864e1bb1cbb8570d39e249bf737675df15295
}
