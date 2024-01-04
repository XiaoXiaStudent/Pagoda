package org.javaboy.pagoda.ordermaster.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javaboy.pagoda.common.core.domain.BaseEntity;

/**
 * <p>
 * 门店信息表
 * </p>
 *
 * @author javaboy
 * @since 2023-04-21
 */
@Data
@TableName("pagoda_store_info")
public class StoreInfo  extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String section;

    private String city;

    private String storeCode;

    private String storeName;

    private LocalDate startTime;

    private Double latitude;

    private Double longitude;


}
