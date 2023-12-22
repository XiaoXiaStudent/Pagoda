package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javaboy.pagoda.common.annotation.Excel;

/**
 * <p>
 *
 * </p>
 *
 * @author javaboy
 * @since 2023-04-20
 */
@Data
@TableName("pagoda_allocation_inventory")
public class AllocationInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "allocation_id", type = IdType.AUTO)
    private Integer allocationId;

    @Excel(name = "商品代码")
    private String productCode;

    @Excel(name = "商品名称")
    private String productName;

    @Excel(name = "商品单位")
    private String productUnit;

    /**
     * 出入库类型 (1-在途入库；2-从外区调入；3-库存校准入库；4-其他入库；5-调出到外区；6-库存校准出库；7-其他出库)
     */
    @Excel(name = "入库类型", height = 18, width = 50)
    private Integer ioType;

    /**
     * 商品数量
     */
    @Excel(name = "入库数量")
    private double quantity;

    /**
     * 商品状态
     */
    private Integer status;

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

    /**
     * 备用字段1
     */
    private int delFlag;




}
