package org.javaboy.pagoda.ordermaster.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodInfoVO {

        private String goodsCode;

        private String goodsName;

        private Double price;

        private Double minShelfQuantity;

        private List<String> selectedStores;

        private Integer numStores;

        private Integer fruitLabel;

        private List<String> goodsCodes;

        private String storeCode;

        private Double distributionPrice;

        private Double orderQuantity;



}
