package com.codingforhappy.dao.redis;

import com.codingforhappy.dao.redis.RedisNamespace.driver;
import com.codingforhappy.dao.redis.RedisNamespace.passenger;
import com.codingforhappy.model.NewTripInfo;
import com.codingforhappy.model.NewTripOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codingforhappy.constants.AccountTypes.DRIVER;
import static com.codingforhappy.constants.AccountTypes.PASSENGER;
import static org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs;


@Service
public class RedisService {
    private static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private StringRedisTemplate template;

    //在redis中增加token->phoneNumber
    public void putTokenToRedis(int type, String token, String phoneNumber) {
        if (type == PASSENGER) {
            String key = getTokenKey(PASSENGER, token);
            template.opsForValue().set(key, phoneNumber);
            template.expire(key, 1, TimeUnit.HOURS);
        } else if (type == DRIVER) {
            String key = getTokenKey(DRIVER, token);
            template.opsForValue().set(key, phoneNumber);
            template.expire(key, 1, TimeUnit.HOURS);
        }
    }

    /**
     * 获取用于查询已登录用户的key
     *
     * @param type
     * @param token
     * @return
     */
    public String getTokenKey(int type, String token) {
        if (type == PASSENGER) {
            return passenger.TOKEN + token;
        } else if (type == DRIVER) {
            return driver.TOKEN + token;
        } else {
            return "";
        }
    }

    /**
     * 尝试从token:type中获取手机号
     *
     * @param key token:type
     * @return
     */
    public String getPhoneNumber(String key) {
        String phoneNum = template.opsForValue().get(key);
        //如果为null，可能redis里的token过期，或者是token错误
        if (phoneNum == null) {
            return "";
        } else
            return phoneNum;
    }

    /**
     * 检查用户是否已经登录，即其token在redis中
     *
     * @param type
     * @param token
     * @return
     */
    public boolean checkLogined(int type, String token) {
        String key = getTokenKey(type, token);
        if (template == null) System.err.println("template is null");
        String value = template.opsForValue().get(key);
        if (value == null)
            return false;
        else
            return true;
    }

    private String getGeoMember(int type, String phoneNumber) {
        if (type == PASSENGER) {
            return phoneNumber;
        } else if (type == DRIVER) {
            return phoneNumber;
        } else {
            System.err.println("TYPE Wrong: at" + RedisService.class.getSimpleName());
            return "";
        }
    }

    private String getGeoKey(int type, String phoneNumber) {
        if (type == PASSENGER) {
            return passenger.POSITION;
        } else if (type == DRIVER) {
            return driver.POSITION;
        } else {
            System.err.println("TYPE Wrong: at" + RedisService.class.getSimpleName());
            return "";
        }
    }

    /**
     * 把坐标加入或者更新到GeoSet.
     * TODO 注意：由于无法使GeoSet的member自动过期，因此GeoSet会不断变大，需要在其他地方定时清除离线用户的数据
     *
     * @param type        "passengers:position"和"drivers:autoAck"
     * @param phoneNumber 手机号
     * @param x           经度
     * @param y           纬度
     */
    public void addPosition(int type, String phoneNumber, double x, double y) {
        Point pos = new Point(x, y);
        String member = getGeoMember(type, phoneNumber);
        String key = getGeoKey(type, phoneNumber);

        template.opsForGeo().add(key, pos, member);
    }

    /**
     * 获取对方的手机号
     * //todo pairs里的内容在行程完成以后需要删除
     *
     * @param selfType        自己的类型
     * @param selfPhoneNumber 自己的手机号
     * @return 对方的手机号
     */
    public String findCounterpartPhoneNumber(int selfType, String selfPhoneNumber) {
        String CounterpartPhoneNumber;
        if (selfType == PASSENGER) {
            CounterpartPhoneNumber = (String) template.opsForHash().get(RedisNamespace.PAIRS,
                    passenger.SELF + selfPhoneNumber);
        } else //if(selfType == DRIVER)
        {
            CounterpartPhoneNumber = (String) template.opsForHash().get(RedisNamespace.PAIRS,
                    driver.SELF + selfPhoneNumber);
        }
        System.out.println("CounterpartPhoneNumber = " + CounterpartPhoneNumber);
        return CounterpartPhoneNumber;
    }

    /**
     * 从redis的GeoSet获得位置
     *
     * @return Point 位置 or null
     */
    public Point getPosition(int type, String phoneNumber) {
        String member = getGeoMember(type, phoneNumber);
        String key = getGeoKey(type, phoneNumber);
        List<Point> result = new ArrayList<>();
        result = template.opsForGeo().position(key, member);
        if (result == null || result.size() != 1) {
            return null;
        }
        return result.get(0);
    }

    /**
     * 开启司机的自动接单
     * 实现：在drivers:autoAck里添加司机手机号和位置
     */
    public void driverAutoAckOn(String phoneNumber, double x, double y) {
        Point pos = new Point(x, y);
        template.opsForGeo().add(driver.AUTO_ACK, pos, phoneNumber);
    }

    /**
     * 在redis的GeoSet里删除司机，关闭司机的自动接单
     * 实现：在drivers:autoAck里添加司机手机号
     */
    public void driverAutoAckOff(String phoneNumber) {
        template.opsForZSet().remove(driver.AUTO_ACK, phoneNumber);
    }

    /**
     * 为乘客匹配开启了自动接单的司机，寻找附近10公里内的司机
     * 如果找到，把匹配的司机和乘客放入redis
     *
     * @param order 乘客的新行程订单
     * @return 司机手机号，若找不到司机，返回空字符串
     */
    public String findAvailableDriver(NewTripOrder order, String passengerPhoneNumber) {
        Point startPos = new Point(order.getStartingPos().getX(), order.getStartingPos().getY());
        Distance distance = new Distance(50000); //5公里
        Circle circle_5km = new Circle(startPos, distance);
        //寻找开启自动接单的司机中最近的
        List<GeoResult<GeoLocation<String>>> list = findNearbyDriver(circle_5km);
        //附近5公里没有司机，继续找附近10公里
        if (list.size() == 0) {
            Circle circle_10km = new Circle(startPos, new Distance(100000));
            list = findNearbyDriver(circle_10km);
            if (list.size() == 0) return "";
        }
        GeoResult<GeoLocation<String>> geoResult = list.get(0); //最近的司机
        System.out.println("获得的匹配司机：" + geoResult);
        Point driverPos = geoResult.getContent().getPoint();
        System.out.println("获得的匹配司机位置 = " + driverPos);
        String driverPhoneNumber = geoResult.getContent().getName();

        //把匹配的司机和乘客放入pairs
        setPair(passengerPhoneNumber, driverPhoneNumber);

        return driverPhoneNumber;
    }

    /**
     * 寻找一定范围内开启自动接单的司机
     *
     * @param circle org.springframework.data.geo.Circle;
     * @return 找到的司机列表，若无法找到，返回空列表
     */
    private List<GeoResult<GeoLocation<String>>> findNearbyDriver(Circle circle) {
        GeoResults<GeoLocation<String>> geoResults =
                template.opsForGeo().radius(driver.AUTO_ACK, circle,
                        newGeoRadiusArgs().sortAscending());
        //可以考虑加上.includeCoordinates()，不过现在用不到
        System.out.println("GeoResults<GeoLocation<String>> in **findNearbyDriver**：" + geoResults);

        if (geoResults != null)
            return geoResults.getContent();
        else
            return new LinkedList<>();
    }

    /**
     * 把司机和乘客的信息放入redis
     */
    private void setPair(String passengerPhoneNumber, String driverPhoneNumber) {
        template.opsForHash().put(RedisNamespace.PAIRS, driver.SELF + driverPhoneNumber,
                passengerPhoneNumber);
        template.opsForHash().put(RedisNamespace.PAIRS, passenger.SELF + passengerPhoneNumber,
                driverPhoneNumber);
    }

    /**
     * 把新行程信息放入redis
     *
     * @param info NewTripInfo
     */
    public void storeNewTripInfo(NewTripInfo info, String passengerPhoneNumber, String driverPhoneNumber) {
        String passengerKey = passenger.NEW_TRIP_INFOS + passengerPhoneNumber;
        String driverKey = driver.NEW_TRIP_INFOS + driverPhoneNumber;
        template.opsForValue().set(passengerKey,
                info.toString());
        template.opsForValue().set(driverKey,
                info.toString());

        //设置2小时后自动清理，防止忘记删除
        template.expire(passengerKey, 2, TimeUnit.HOURS);
        template.expire(driverKey, 2, TimeUnit.HOURS);
    }

    /**
     * 清除之前存入的新行程信息
     */
    public void clearNewTripInfo(String passengerPhoneNumber, String driverPhoneNumber) {
        template.delete(passenger.NEW_TRIP_INFOS + passengerPhoneNumber);
        template.delete(driver.NEW_TRIP_INFOS + driverPhoneNumber);
    }

    /**
     * 根据手机号寻找redis中的json字符串，反序列化得到对象
     *
     * @param type        类型，司机或乘客
     * @param phoneNumber 手机号
     * @return 反序列化得到的对象
     * @throws Exception "can't deserialize to object" or "can't find NewTripInfo in redis"
     */
    public NewTripInfo getNewTripInfo(int type, String phoneNumber) throws Exception {
        Exception e = new Exception("can't deserialize to object");
        Exception notfoundException = new Exception("can't find NewTripInfo in redis");
        String key;
        if (type == PASSENGER) {
            key = passenger.NEW_TRIP_INFOS + phoneNumber;
        } else if (type == DRIVER) {
            key = driver.NEW_TRIP_INFOS + phoneNumber;
        } else
            return new NewTripInfo();

        //获取NewTripInfo的json字符串
        String jsonString = template.opsForValue().get(key);
        logger.info("从redis获得的json字符串={}", jsonString);
        if (jsonString == null || jsonString.isEmpty())
            throw notfoundException;

        //反序列化
        ObjectMapper mapper = new ObjectMapper();
        NewTripInfo info = mapper.readValue(jsonString, NewTripInfo.class);
        if (info == null || info.toString().isEmpty())
            throw e;
        logger.info("生成的NewTripInfo实例={}", info.toString());
        return info;
    }
}
