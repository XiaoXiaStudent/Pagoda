package org.javaboy.pagoda.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.javaboy.pagoda.common.annotation.Log;
import org.javaboy.pagoda.common.core.controller.BaseController;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.javaboy.pagoda.common.core.domain.entity.SysDictData;
import org.javaboy.pagoda.common.core.page.TableDataInfo;
import org.javaboy.pagoda.common.enums.BusinessType;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.common.utils.poi.ExcelUtil;
import org.javaboy.pagoda.system.service.ISysDictDataService;
import org.javaboy.pagoda.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 *
 * @author pagoda
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {
        @Autowired
        private ISysDictDataService dictDataService;

        @Autowired
        private ISysDictTypeService dictTypeService;

        @PreAuthorize("hasPermissions('system:dict:list')")
        @GetMapping("/list")
        public TableDataInfo list(SysDictData dictData) {
                startPage();
                List<SysDictData> list = dictDataService.selectDictDataList(dictData);
                return getDataTable(list);
        }

        @Log(title = "字典数据", businessType = BusinessType.EXPORT)
        @PreAuthorize("hasPermissions('system:dict:export')")
        @PostMapping("/export")
        public void export(HttpServletResponse response, SysDictData dictData) {
                List<SysDictData> list = dictDataService.selectDictDataList(dictData);
                ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
                util.exportExcel(response, list, "字典数据");
        }

        /**
         * 查询字典数据详细
         */
        @PreAuthorize("hasPermissions('system:dict:query')")
        @GetMapping(value = "/{dictCode}")
        public AjaxResult getInfo(@PathVariable Long dictCode) {
                return AjaxResult.success(dictDataService.selectDictDataById(dictCode));
        }

        /**
         * 根据字典类型查询字典数据信息
         */
        @GetMapping(value = "/type/{dictType}")
        public AjaxResult dictType(@PathVariable String dictType) {
                List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
                if (StringUtils.isNull(data)) {
                        data = new ArrayList<SysDictData>();
                }
                return AjaxResult.success(data);
        }

        /**
         * 新增字典类型
         */
        @PreAuthorize("hasPermissions('system:dict:add')")
        @Log(title = "字典数据", businessType = BusinessType.INSERT)
        @PostMapping
        public AjaxResult add(@Validated @RequestBody SysDictData dict) {
                dict.setCreateBy(getUsername());
                return toAjax(dictDataService.insertDictData(dict));
        }

        /**
         * 修改保存字典类型
         */
        @PreAuthorize("hasPermissions('system:dict:edit')")
        @Log(title = "字典数据", businessType = BusinessType.UPDATE)
        @PutMapping
        public AjaxResult edit(@Validated @RequestBody SysDictData dict) {
                dict.setUpdateBy(getUsername());
                return toAjax(dictDataService.updateDictData(dict));
        }

        /**
         * 删除字典类型
         */
        @PreAuthorize("hasPermissions('system:dict:remove')")
        @Log(title = "字典类型", businessType = BusinessType.DELETE)
        @DeleteMapping("/{dictCodes}")
        public AjaxResult remove(@PathVariable Long[] dictCodes) {
                dictDataService.deleteDictDataByIds(dictCodes);
                return success();
        }
}
