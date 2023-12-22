package org.javaboy.pagoda.ordermaster.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShoppingVO {

        @TableId(value = "cart_id", type = IdType.AUTO)
        private Integer cartId;

        private String goodsCode;

        private Double orderQuantity;

        private String storeCode;

        private Double distributionPrice;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 销售单位
         */
        private String salesUnit;

        /**
         * 批量数量
         */
        private Double batchQuantity;

        /**
         * 果品标签
         */
        private Integer fruitLabel;

        private Double totalPrice;




}
