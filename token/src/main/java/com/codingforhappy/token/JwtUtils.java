package com.codingforhappy.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import net.minidev.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoidXV1IiwiZXgiOjE1NDU2NTQ0OTUyMDh9.wZvjPuAjDT-x2Pjldlq27YlcKZ0LUMiEwm-m4C2Y_Kw"
    //密钥 HS256是对称加密，因此只有一个
    private static final byte[] SECRET = "".getBytes();

    private static final JWSHeader header =
            new JWSHeader(JWSAlgorithm.HS256, JOSEObjectType.JWT,
                    null, null, null, null, null, null, null, null, null, null, null);

    public static String createToken(Map<String, Object> payload) {
        String tokenString = null;
        //设置默认过期时间
        if(!payload.containsKey("ext"))
            payload.put("ext", new Date().getTime() + 1000 * 3600 * 24 * 7);

        // 创建一个 JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(new JSONObject(payload)));
        // 用MAC签名 Message Authentication Code，支持3种算法
        // JWSAlgorithm.HS256
        // JWSAlgorithm.HS384
        // JWSAlgorithm.HS512
        try {
            jwsObject.sign(new MACSigner(SECRET)); //
            tokenString = jwsObject.serialize();
        } catch (JOSEException e) {
            System.err.println("签名失败:" + e.getMessage());
            e.printStackTrace();
        }
        return tokenString;
    }

    /**
     * 校验token是否合法，返回Map集合,集合中主要包含    state状态码   data鉴权成功后从token中提取的数据
     * 该方法在过滤器中调用，每次请求API时都校验
     *
     * @param token
     * @return Map<String, Object> {data={"ext":1548667174984,"phoneNumber":"13017833333"}, state=VALID}
     */
    public static Map<String, Object> verifyToken(String token) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            Payload payload = jwsObject.getPayload();
            JWSVerifier verifier = new MACVerifier(SECRET);

            if (jwsObject.verify(verifier)) {
                JSONObject jsonObject = payload.toJSONObject();
                // token校验成功（此时没有校验是否过期）
                resultMap.put("state", TokenState.VALID.toString());
                // 若payload包含ext字段，则校验是否过期
                if (jsonObject.containsKey("ext")) {
                    long extTime = Long.valueOf(jsonObject.get("ext").toString());
                    long curTime = new Date().getTime();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // 过期了
                    if (curTime > extTime) {
                        resultMap.clear();
                        resultMap.put("state", TokenState.EXPIRED.toString());
                    }
                }
                resultMap.put("data", jsonObject);
            } else {
                // 校验失败
                resultMap.put("state", TokenState.INVALID.toString());
            }

        } catch (Exception e) {
            //e.printStackTrace();
            // token格式不合法导致的异常
            resultMap.clear();
            resultMap.put("state", TokenState.INVALID.toString());
        }
        return resultMap;
    }

    public static Map<String, Object> getPayload(String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            Payload payload = JWSObject.parse(token).getPayload();
            jsonObject = payload.toJSONObject();
        } catch (Exception e) {
            System.err.println("token错误:" + token);
        }
        return jsonObject;
    }

    public static String getPhoneNumber(String token) {
        return (String) getPayload(token).get("phoneNumber");
    }

    public static void main(String[] args) {
        Map<String, Object> m = new HashMap<>();
        m.put("phoneNumber", "13012310000");
        m.put("ext", 1586670861792L); //毫秒
        String jwt_s = JwtUtils.createToken(m);

        Map<String, Object> resultM = JwtUtils.verifyToken(jwt_s);
        System.out.println(jwt_s);
        System.out.println(resultM);
        //长时间有效token:
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHQiOjE1ODY2NzA4NjE3OTIsInBob25lTnVtYmVyIjoiMTMwMTIzMTAwMDAifQ.xSntHcWk5xHdwi5q6GzWK0ctFngyUJ1KsLHvx065N3w
    }

}
