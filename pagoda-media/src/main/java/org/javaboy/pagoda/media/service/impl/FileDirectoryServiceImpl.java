package org.javaboy.pagoda.media.service.impl;

import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.common.utils.SecurityUtils;
import org.javaboy.pagoda.media.vo.FileDirectoryVO;
import org.javaboy.pagoda.media.entity.FileDirectory;
import org.javaboy.pagoda.media.mapper.FileDirectoryMapper;
import org.javaboy.pagoda.media.service.IFileDirectoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件夹表 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-05-18
 */
@Service
public class FileDirectoryServiceImpl extends ServiceImpl<FileDirectoryMapper, FileDirectory> implements IFileDirectoryService {

        @Resource
        FileDirectoryMapper fileDirectoryMapper;

        @Override
        public AjaxResult listDirectory() {

                // 获取所有文件夹
                List<FileDirectory> allDirectories = fileDirectoryMapper.selectList(null);

                // 构建根节点
                List<FileDirectory> rootDirectories = allDirectories.stream().filter(d -> d.getParentId() == 0).collect(Collectors.toList());

                // 递归构建每个根节点的子文件夹列表
                rootDirectories.forEach(root -> {
                        buildChildren(root, allDirectories);
                });

                // 返回结果
                return AjaxResult.success(rootDirectories);
        }

        @Override
        public AjaxResult add(FileDirectory fileDirectory) {
                // 获取parentId对应的目录
                FileDirectory parentDirectory = fileDirectoryMapper.selectById(fileDirectory.getParentId());

                // 检查parentId是否有效
                if (parentDirectory == null) {
                        return AjaxResult.error("父目录ID无效");
                }

                // 生成新目录的ancestors
                String ancestors = parentDirectory.getAncestors() + "," + fileDirectory.getParentId();
                fileDirectory.setAncestors(ancestors);

                // 设置其他必要的字段
                fileDirectory.setDelFlag("0");
                fileDirectory.setStatus("0");

                // 设置创建者、创建时间、更新者、更新时间
                String currentUser = SecurityUtils.getUsername(); // 获取当前用户的服务
                LocalDateTime currentTime = LocalDateTime.now(); // 获取当前时间
                fileDirectory.setCreateBy(currentUser);
                fileDirectory.setCreateTime(currentTime);
                fileDirectory.setUpdateBy(currentUser);
                fileDirectory.setUpdateTime(currentTime);

                // 保存新的目录
                int result = fileDirectoryMapper.insert(fileDirectory);

                // 检查结果
                if (result > 0) {
                        return AjaxResult.success();
                } else {
                        return AjaxResult.error("添加目录失败");
                }
        }

        /**
         * 递归构建子文件夹列表
         */
        private void buildChildren(FileDirectory parentDirectory, List<FileDirectory> allDirectories) {
                List<FileDirectory> children = allDirectories.stream().filter(d -> d.getParentId().equals(parentDirectory.getId())).collect(Collectors.toList());
                parentDirectory.setChildren(children);
                children.forEach(child -> {
                        buildChildren(child, allDirectories);
                });
        }
}
