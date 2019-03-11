package com.codingforhappy.dao.redis;

public class RedisNamespace {
    public static final String PAIRS = "pairs"; //Hash

    public interface passenger {
        //前缀
        String TOKEN = "passenger:token:"; //KV
        String SELF = "passenger:"; //KV
        String NEW_TRIP_INFOS = "passenger:new_trip_info:"; //KV

        //集合
        String POSITION = "passengers:position"; //GeoSet
    }

    public interface driver {
        //前缀
        String TOKEN = "driver:token:"; //KV
        String SELF = "driver:"; //KV
        String NEW_TRIP_INFOS = "driver:new_trip_info:"; //KV


        //集合
        String POSITION = "drivers:autoAck"; //GeoSet
        String AUTO_ACK = "drivers:autoAck"; //GeoSet
    }
}
