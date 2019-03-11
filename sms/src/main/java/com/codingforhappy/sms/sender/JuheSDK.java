package com.codingforhappy.sms.sender;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


public class JuheSDK {
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    public static JuheReturnValue send(String phoneNum, String tpl_id, String tpl_value, String key){
        JuheReturnValue rv = null;
        String result =null;
        String url ="http://v.juhe.cn/sms/send";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("mobile", phoneNum);//接受短信的用户手机号码
        params.put("tpl_id",tpl_id);//您申请的短信模板ID，根据实际情况修改
//        params.put("tpl_value","#code#=765432");//您设置的模板变量，根据实际情况修改
        params.put("tpl_value",tpl_value);//您设置的模板变量，根据实际情况修改
        params.put("key",key);//应用APPKEY(应用详细页查询)
        try {
            result = net(url, params, "GET");
            ObjectMapper objectMapper = new ObjectMapper();
            rv = objectMapper.readValue(result, JuheReturnValue.class);
        } catch (Exception e) {
            e.printStackTrace();
            rv = new JuheReturnValue();
            rv.setError_code(JuheReturnValue.ERROR_CODE.SYSTEM_ERROR);
            rv.setReason(JuheReturnValue.ERROR_MESSAGE.SYSTEM_ERROR);
        }
        return rv;
    }

    public static void main(String[] args) throws IOException {
        String result = "{\"reason\":\"操作成功\",\"result\":{\"sid\":\"b82effb1-72f5-4977-8acc-a00587f725c9_111134220\",\"fee\":1,\"count\":1},\"error_code\":0}";
        System.out.println(result);
        ObjectMapper objectMapper = new ObjectMapper();
        JuheReturnValue rv = objectMapper.readValue(result, JuheReturnValue.class);
        if(rv.getError_code()==0){
            System.out.println(rv.getResult());
        }else{
            System.out.println(rv.getError_code()+":"+rv.getReason());
        }
    }

    /**
     *
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

