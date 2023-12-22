package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-02
 */
public interface IOrdersService extends IService<Orders> {

        AjaxResult order(String storeCode);
}
