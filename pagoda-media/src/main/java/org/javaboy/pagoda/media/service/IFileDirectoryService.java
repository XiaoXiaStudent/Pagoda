package org.javaboy.pagoda.media.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.media.entity.FileDirectory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文件夹表 服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-18
 */
public interface IFileDirectoryService extends IService<FileDirectory> {

        AjaxResult listDirectory();

        AjaxResult add(FileDirectory fileDirectory);
}
