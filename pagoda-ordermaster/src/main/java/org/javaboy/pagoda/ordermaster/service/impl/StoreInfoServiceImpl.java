package org.javaboy.pagoda.ordermaster.service.impl;

import org.javaboy.pagoda.common.annotation.DataScope;
import org.javaboy.pagoda.common.annotation.DataSource;
import org.javaboy.pagoda.common.annotation.DataStoreScope;
import org.javaboy.pagoda.common.enums.DataSourceType;
import org.javaboy.pagoda.ordermaster.entity.StoreInfo;
import org.javaboy.pagoda.ordermaster.mapper.StoreInfoMapper;
import org.javaboy.pagoda.ordermaster.service.IStoreInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 门店信息表 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
@Service
public class StoreInfoServiceImpl extends ServiceImpl<StoreInfoMapper, StoreInfo> implements IStoreInfoService {

        @Resource
        StoreInfoMapper storeInfoMapper;


        @Override
        public List<StoreInfo> storeinfoByPage(StoreInfo storeInfo) {
                return storeInfoMapper.storeinfoByPage(storeInfo);
        }

        @Override
        @DataStoreScope
        public List<StoreInfo> getStores(StoreInfo storeInfo) {
                return storeInfoMapper.getStores(storeInfo);
        }

        @Override
        @DataSource(DataSourceType.master)
        public List<StoreInfo> getStoresByDataSource() {
                return storeInfoMapper.selectList(null);
        }

}
