package org.javaboy.pagoda.ordermaster.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.javaboy.pagoda.ordermaster.vo.ShoppingVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
public interface IShoppingCartService extends IService<ShoppingCart> {

        List<ShoppingVO> selectShoppingList(String storeCode);

        void deleteItemsByStoreCode(String storeCode);

        AjaxResult manageShoppingCart(GoodInfoVO goodInfoVO);
}
