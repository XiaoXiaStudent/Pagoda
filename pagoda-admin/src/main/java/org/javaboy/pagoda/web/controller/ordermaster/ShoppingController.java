package org.javaboy.pagoda.web.controller.ordermaster;

import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.ordermaster.service.IShoppingCartService;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.javaboy.pagoda.ordermaster.vo.ShoppingVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shopping")
public class ShoppingController  extends BaseController {

        @Resource
        IShoppingCartService shoppingCartService;

        @PostMapping("/list/{storeCode}")
        public TableDataInfo list(@PathVariable String storeCode) {

                startPage();

                List<ShoppingVO> list = shoppingCartService.selectShoppingList(storeCode);

                return getDataTable(list);

        }

        @DeleteMapping("/delete/{storeCode}/{goodsCode}")
        public AjaxResult deleteCartItem(@PathVariable String storeCode, @PathVariable String goodsCode) {
                GoodInfoVO goodInfoVO = new GoodInfoVO();
                goodInfoVO.setStoreCode(storeCode);
                goodInfoVO.setGoodsCode(goodsCode);
                goodInfoVO.setOrderQuantity(null);
                return shoppingCartService.manageShoppingCart(goodInfoVO);
        }

        @PutMapping("/update/{storeCode}/{goodsCode}/{orderQuantity}")
        public AjaxResult updateCartItem(@PathVariable String storeCode, @PathVariable String goodsCode, @PathVariable Double orderQuantity) {
                GoodInfoVO goodInfoVO = new GoodInfoVO();
                goodInfoVO.setStoreCode(storeCode);
                goodInfoVO.setGoodsCode(goodsCode);
                goodInfoVO.setOrderQuantity(orderQuantity);
                return shoppingCartService.manageShoppingCart(goodInfoVO);
        }

}
