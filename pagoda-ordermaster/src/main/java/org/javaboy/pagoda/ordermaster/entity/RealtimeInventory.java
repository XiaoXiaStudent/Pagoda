package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 实时库存ES
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
@Getter
@Setter
@TableName("pagoda_realtime_inventory")
public class RealtimeInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 爬取日期
     */
    private LocalDate date;

    /**
     * 配送中心代码
     */
    private String dualOrgCode;

    /**
     * 配送中心名称
     */
    private String dualOrgName;

    /**
     * 库房名称
     */
    private String depotName;

    /**
     * 库房代码
     */
    private String depotCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品代码
     */
    private String goodsCode;

    /**
     * 商品分类
     */
    private String goodsClassName;

    /**
     * 商品单位
     */
    private String invUnitName;

    /**
     * 账面库存数量
     */
    private Double onhandQty;

    /**
     * 可用库存数量
     */
    private Double availableQty;

    /**
     * 实时库存成本价

     */
    private Double realPrice;

    /**
     * 可用库存金额
     */
    private Double costAmt;

    /**
     * 三日均销量
     */
    private Double d3AvgSellQty;

    /**
     * 库销比
     */
    private String stockSellRatio;

    /**
     * 可用库存成本价
     */
    private Double finPrice;


}
