package org.javaboy.pagoda.ordermaster.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 门店订货页面商品展示请求体
 */
@Data
public class StoreOrderR {

        // 商品代码
        private String goodsCode;

        @NotBlank(message = "门店代码不能为空")
        private String depotCode;

}
