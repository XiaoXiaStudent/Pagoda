package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品与门店关联表
 * </p>
 *
 * @author javaboy
 * @since 2023-04-24
 */
@Getter
@Setter
@TableName("goods_store_info")
public class GoodsStoreInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String goodsId;

    private String storeId;

    /**
     * 配送价格
     */
    private Double distributionPrice;

    /**
     * 最小货架量
     */
    private Double minShelfQuantity;

    private LocalDateTime updateTime;

    private String updateBy;

    private Integer delFlag;


}
