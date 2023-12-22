package org.javaboy.pagoda.ordermaster.mapper;

import org.javaboy.pagoda.ordermaster.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.javaboy.pagoda.ordermaster.vo.ShoppingVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

        List<ShoppingVO> selectShoppingList(String shoppingVO);


}
