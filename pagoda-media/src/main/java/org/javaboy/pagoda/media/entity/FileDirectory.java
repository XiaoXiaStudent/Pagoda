package org.javaboy.pagoda.media.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件夹表
 * </p>
 *
 * @author javaboy
 * @since 2023-05-18
 */
@Data
@TableName("file_directory")
public class FileDirectory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件夹ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 祖先文件夹列表
     */
    private String ancestors;

    /**
     * 文件夹名称
     */
    private String fileName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 文件夹状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<FileDirectory> children;


}
