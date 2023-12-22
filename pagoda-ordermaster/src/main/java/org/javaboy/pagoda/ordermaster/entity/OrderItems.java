package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author javaboy
 * @since 2023-05-02
 */
@Data
@TableName("order_items")
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单商品ID
     */
    @TableId(value = "order_item_id", type = IdType.AUTO)
    private Integer orderItemId;

    /**
     * 订单ID
     */
    private long orderId;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Double quantity;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 小计金额
     */
    private Double subtotal;

    /**
     * 销售单位
     */
    private String salesUnit;

    /**
     * 批量数量
     */
    private Double batchQuantity;

    /**
     * 供应趋势
     */
    private String supplyTrend;

    /**
     * 产地
     */
    private String productionPlace;

    /**
     * 配送周期
     */
    private String deliveryCycle;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 额外字段1
     */
    private String extraField1;

    /**
     * 额外字段2
     */
    private String extraField2;

    /**
     * 逻辑删除标识
     */
    private Integer delFlag;


}
