package org.javaboy.pagoda.media.service;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.media.entity.MediaFiles;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 媒资信息 服务类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-17
 */
public interface IMediaFilesService extends IService<MediaFiles> {

        AjaxResult getFilesInDirectory(Long directoryId);

        AjaxResult upload(byte[] bytes, MediaFiles mediaFiles);

        AjaxResult uploadchunk(byte[] bytes, int chunk, String fileMd5, String contentType);

        AjaxResult checkChunk(String fileMd5, int chunk);

        AjaxResult mergechunks(String fileMd5, String fileName, int chunkTotal, Long directoryId);

}
