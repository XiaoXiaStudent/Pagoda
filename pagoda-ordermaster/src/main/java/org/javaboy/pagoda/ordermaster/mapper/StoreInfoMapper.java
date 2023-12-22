package org.javaboy.pagoda.ordermaster.mapper;

import org.javaboy.pagoda.ordermaster.entity.StoreInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 门店信息表 Mapper 接口
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
public interface StoreInfoMapper extends BaseMapper<StoreInfo> {

        List<StoreInfo> storeinfoByPage(StoreInfo storeInfo);

        List<StoreInfo> getStores(StoreInfo storeInfo);

}
