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
 *
 * </p>
 *
 * @author javaboy
 * @since 2023-04-26
 */
@Getter
@Setter
@TableName("pagoda_distribution_inventory")
public class DistributionInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "allocation_id", type = IdType.AUTO)
    private Integer allocationId;

    /**
     * 商品代码
     */
    private String productCode;


    /**
     * 商品数量
     */
    private Double quantity;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 备注
     */
    private String remark;

    private Integer type;

    private Integer delFlag;




}
