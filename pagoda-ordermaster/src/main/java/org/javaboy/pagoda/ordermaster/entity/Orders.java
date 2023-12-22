package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@Getter
@Setter
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private long orderId;

    /**
     * 门店编码
     */
    private String storeCode;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 配送地址
     */
    private String shippingAddress;

    /**
     * 配送费用
     */
    private Double shippingFee;

    /**
     * 订单总金额
     */
    private Double totalAmount;

    /**
     * 订单创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 订单更新时间
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
