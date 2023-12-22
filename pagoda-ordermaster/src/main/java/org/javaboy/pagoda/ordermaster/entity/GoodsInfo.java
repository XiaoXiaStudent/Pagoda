package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * 订货系统商品表
 * </p>
 *
 * @author javaboy
 * @since 2023-04-23
 */
@Data
@TableName("pagoda_goods_info")
public class GoodsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 销售单位
     */
    private String salesUnit;

    /**
     * 批量数量
     */
    private Double batchQuantity;



    /**
     * 货源走势
     */
    private String supplyTrend;

    /**
     * 进口标识
     */
    private String importFlag;

    /**
     * 产地
     */
    private String productionPlace;

    /**
     * 配送周期
     */
    private String deliveryCycle;

    /**
     * 最后修改人
     */
    private String lastModifiedBy;

    /**
     * 最后修改时间
     */
    private LocalDate lastModifiedTime;

    /**
     * 果品标签
     */
    private Integer fruitLabel;

    /**
     * 商品类型
     */
    private String type;

    private String relatedGoods;



}
