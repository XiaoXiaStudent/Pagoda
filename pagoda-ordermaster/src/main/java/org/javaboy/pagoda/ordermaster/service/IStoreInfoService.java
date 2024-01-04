package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.ordermaster.entity.StoreInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 门店信息表 服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
public interface IStoreInfoService extends IService<StoreInfo> {

        List<StoreInfo> storeinfoByPage(StoreInfo storeInfo);

        List<StoreInfo> getStores(StoreInfo storeInfo);

        List<StoreInfo> getStoresByDataSource();
}
