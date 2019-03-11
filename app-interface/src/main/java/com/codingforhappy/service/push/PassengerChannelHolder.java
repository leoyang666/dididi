package com.codingforhappy.service.push;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PassengerChannelHolder {
    private static final Logger logger = LoggerFactory.getLogger(PassengerChannelHolder.class);
    //存放id -> Channel
    private static final Map<String, NioSocketChannel> PASSENGER_MAP = new ConcurrentHashMap<>(16);

    private PassengerChannelHolder() {
    }

    public static void put(String id, NioSocketChannel socketChannel) {
        PASSENGER_MAP.put(id, socketChannel);
//        logger.info("put: 现在MAP里有{}个entity", DRIVER_MAP.size());
    }

    /**
     * @param id phoneNumber
     * @return 找不到返回null，否则返回id对应的NioSocketChannel
     */
    static NioSocketChannel get(String id) {
//        logger.info("get: 现在MAP里有{}个entity", DRIVER_MAP.size());
        return PASSENGER_MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return PASSENGER_MAP;
    }

    /**
     * 删除Value为Chennel的entry，O(n)
     */
    public static void remove(NioSocketChannel nioSocketChannel) {
        PASSENGER_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> PASSENGER_MAP.remove(entry.getKey()));
    }

    /**
     * 删除Key为id的entry，O(1)
     */
    static void remove(String id) {
        PASSENGER_MAP.remove(id);
    }

    static boolean contains(String id) {
        return PASSENGER_MAP.containsKey(id);
    }
}
