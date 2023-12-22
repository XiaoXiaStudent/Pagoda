package org.javaboy.pagoda.ordermaster.vo;

import lombok.Data;
import org.javaboy.pagoda.common.annotation.Excel;

@Data
public class AssignmentVO {

        private Integer allocationId;

        @Excel(name = "果品代码")
        private String productCode;

        @Excel(name = "果品代码")
        private String goodsName;

        @Excel(name = "业务类型", readConverterExp = "1=指定分货,2=自由订货")
        private Integer type;

        @Excel(name = "配送库存")
        private Double quantity;




}
