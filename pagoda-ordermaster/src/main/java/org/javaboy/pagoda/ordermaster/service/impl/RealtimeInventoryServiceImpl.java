package org.javaboy.pagoda.ordermaster.service.impl;

import org.javaboy.pagoda.ordermaster.entity.RealtimeInventory;
import org.javaboy.pagoda.ordermaster.mapper.RealtimeInventoryMapper;
import org.javaboy.pagoda.ordermaster.service.IRealtimeInventoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 实时库存ES 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
@Service
public class RealtimeInventoryServiceImpl extends ServiceImpl<RealtimeInventoryMapper, RealtimeInventory> implements IRealtimeInventoryService {

}
