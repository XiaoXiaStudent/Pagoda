package org.javaboy.pagoda.ordermaster.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.xml.stream.Location;
import java.time.LocalDateTime;

/**
 * 门店订货页面商品展示
 */
@Data
public class StoreOrderVO {

        // 商品代码
        private String goodsCode;

        // 商品名称
        private String goodsName;

        private double quantity;

        // 配送价格
        private double distributionPrice;

        // 批量数量
        private double batchQuantity;

        // 货源状态
        private String supplyStatus;

        // 产地
        private String productionPlace;

        // 配送周期
        private String deliveryCycle;

        // 货架期
        private double shelfLife;

        // 品质状况描述
        private String qualityDescription;

        // 货源走势
        private String supplyTrend;

        @TableField(value = "availableQty")
        private double availableQty;

        @TableField(value = "stockSellRatio")
        private double stockSellRatio;

        //销售单位
        private String salesUnit;

        private LocalDateTime updatedTime;













}
