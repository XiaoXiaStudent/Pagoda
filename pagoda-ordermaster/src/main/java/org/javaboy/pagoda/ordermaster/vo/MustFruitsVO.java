package org.javaboy.pagoda.ordermaster.vo;

import lombok.Data;

@Data
public class MustFruitsVO {
        private String productCode;
        private String goodsName;
        private String salesUnit;
        private String relatedGoods;
        private Double quantity;
        private String fruitLabel;
        private Double distributionPrice;
        private Double minShelfQuantity;
}
