package com.codingforhappy.service.push;

import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverChannelHolder {
    private static final Logger logger = LoggerFactory.getLogger(DriverChannelHolder.class);
    //存放id -> Channel
    private static final Map<String, NioSocketChannel> DRIVER_MAP = new ConcurrentHashMap<>(16);

    private DriverChannelHolder() {
    }

    public static void put(String id, NioSocketChannel socketChannel) {
        DRIVER_MAP.put(id, socketChannel);
//        logger.info("put: 现在MAP里有{}个entity", DRIVER_MAP.size());
    }

    /**
     * @param id phoneNumber
     * @return 找不到返回null，否则返回id对应的NioSocketChannel
     */
    static NioSocketChannel get(String id) {
//        logger.info("get: 现在MAP里有{}个entity", DRIVER_MAP.size());
        return DRIVER_MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return DRIVER_MAP;
    }

    /**
     * 删除Value为Chennel的entry，O(n)
     *
     * @param nioSocketChannel
     */
    public static void remove(NioSocketChannel nioSocketChannel) {
        DRIVER_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> DRIVER_MAP.remove(entry.getKey()));
    }

    /**
     * 删除Key为id的entry，O(1)
     *
     * @param id
     */
    static void remove(String id) {
        DRIVER_MAP.remove(id);
    }

    static boolean contains(String id) {
        return DRIVER_MAP.containsKey(id);
    }
}
