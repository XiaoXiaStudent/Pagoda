package org.javaboy.pagoda.ordermaster.service.impl;

import org.javaboy.pagoda.ordermaster.entity.OrderItems;
import org.javaboy.pagoda.ordermaster.mapper.OrderItemsMapper;
import org.javaboy.pagoda.ordermaster.service.IOrderItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-02
 */
@Service
public class OrderItemsServiceImpl extends ServiceImpl<OrderItemsMapper, OrderItems> implements IOrderItemsService {

}
