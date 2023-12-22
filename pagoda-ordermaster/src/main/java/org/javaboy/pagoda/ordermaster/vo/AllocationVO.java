package org.javaboy.pagoda.ordermaster.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.javaboy.pagoda.common.annotation.Excel;

@Data
public class AllocationVO {

        @Excel(name = "商品代码")
        private String productCode;

        @Excel(name = "商品名称")
        private String productName;

        @Excel(name = "商品单位")
        private String productUnit;

        /**
         * 出入库类型 (1-在途入库；2-从外区调入；3-库存校准入库；4-其他入库；5-调出到外区；6-库存校准出库；7-其他出库)
         */

        @Excel(name = "入库类型",height = 18, width=50)
        @TableField(value = "io_type")
        private String ioTypeList;

        @Excel(name = "入库数量")
        @TableField(value = "quantity")
        private String quantityList;

        private double totalQuantity;


        private String ioTypeQuantity;

        private Integer status;



}
