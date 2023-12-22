package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javaboy.pagoda.common.core.domain.BaseEntity;

/**
 * <p>
 *
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
@Data
@TableName("shopping_cart")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cart_id", type = IdType.AUTO)
    private Integer cartId;

    private String goodsCode;

    private Double orderQuantity;

    private String storeCode;

    private Double distributionPrice;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private int delFlag;
}
