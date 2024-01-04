package org.javaboy.pagoda;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javaboy.pagoda.common.annotation.DataSource;
import org.javaboy.pagoda.common.core.redis.RedisCache;
import org.javaboy.pagoda.common.enums.DataSourceType;
import org.javaboy.pagoda.common.utils.JsonUtil;
import org.javaboy.pagoda.datadashboard.entity.PagodaStoreInfo;
import org.javaboy.pagoda.ordermaster.entity.StoreInfo;
import org.javaboy.pagoda.ordermaster.mapper.StoreInfoMapper;
import org.javaboy.pagoda.ordermaster.service.IStoreInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class MulitDataSource {

        @Resource
        StoreInfoMapper storeInfoMapper;

        @Resource
        IStoreInfoService storeInfoService;

        @Resource
        RedisCache redisCache;

        @Resource
        ConversionService conversionService;

        @Resource
        public RedisTemplate redisTemplate;


        @Test
        public void testRedisSerialization() {
                String key = "testKey";
                PagodaStoreInfo storeInfo = new PagodaStoreInfo();
                storeInfo.setCity("qingdao");// 假设这是一个有效的StoreInfo对象
                // 存储对象
                redisTemplate.opsForValue().set(key, storeInfo);

                // 立即读取对象
                Object o = redisTemplate.opsForValue().get(key);
                PagodaStoreInfo pagodaStoreInfo = objectMapper.convertValue(o, PagodaStoreInfo.class);

                System.out.println(o);
        }

        @Test

        void contextLoads() {

                storeInfoService.getStoresByDataSource().forEach(System.out::println);

        }

        @Resource
        ObjectMapper objectMapper;

        @Test
        void testRedis() {
                List<Object> storeInfo = redisCache.getCacheList("storeInfo");
                List<PagodaStoreInfo> collect = storeInfo.stream().map(m -> objectMapper.convertValue(m, PagodaStoreInfo.class)).collect(Collectors.toList());

                System.out.println(storeInfo);
        }



}
