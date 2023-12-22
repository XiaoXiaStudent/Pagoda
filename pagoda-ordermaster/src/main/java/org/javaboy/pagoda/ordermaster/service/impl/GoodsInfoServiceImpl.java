package org.javaboy.pagoda.ordermaster.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.javaboy.pagoda.common.core.domain.AjaxResult;
import org.javaboy.pagoda.ordermaster.entity.GoodsInfo;
import org.javaboy.pagoda.ordermaster.entity.GoodsStoreInfo;
import org.javaboy.pagoda.ordermaster.mapper.GoodsInfoMapper;
import org.javaboy.pagoda.ordermaster.service.IGoodsInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.javaboy.pagoda.ordermaster.service.IGoodsStoreInfoService;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendR;
import org.javaboy.pagoda.ordermaster.vo.DistributeSendVO;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoR;
import org.javaboy.pagoda.ordermaster.vo.GoodInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订货系统商品表 服务实现类
 * </p>
 *
 * @author javaboy
 * @since 2023-04-23
 */
@Service
public class GoodsInfoServiceImpl extends ServiceImpl<GoodsInfoMapper, GoodsInfo> implements IGoodsInfoService {

        @Resource
        GoodsInfoMapper goodsInfoMapper;

        @Resource
        IGoodsStoreInfoService goodsStoreInfoService;

        @Override
        public List<GoodInfoR> getGoodInfoList(GoodInfoVO goodsInfo) {

                // 查询基础果品信息
                List<GoodInfoR> goodBaseInfoList = goodsInfoMapper.getGoodBaseInfoList(goodsInfo);

                List<GoodInfoVO> priceAndQuantity = goodsInfoMapper.getPriceAndQuantity(goodsInfo);

                // 遍历 goodBaseInfoList
                for (GoodInfoR goodInfoR : goodBaseInfoList) {
                        List<GoodInfoVO> goodInfoVOS = new ArrayList<>();
                        // 遍历 priceAndQuantity
                        for (GoodInfoVO goodInfoVO : priceAndQuantity) {
                                // 检查 goodsCode 是否匹配
                                if (goodInfoR.getGoodsCode().equals(goodInfoVO.getGoodsCode())) {
                                        goodInfoVOS.add(goodInfoVO);
                                }
                        }
                        goodInfoR.setGoodInfoVOS(goodInfoVOS);
                }

                return goodBaseInfoList;

        }

        @Override
        public GoodsInfo getGoodInfoByGoodCode(String goodsCode) {
                GoodsInfo one = lambdaQuery().eq(GoodsInfo::getGoodsCode, goodsCode).one();
                return one;
        }

        //添加配送价格
        @Override
        @Transactional
        public AjaxResult addPriceAndQuantityGoods(GoodInfoVO goodInfoVO) {

                List<GoodsStoreInfo> insertList = new ArrayList<>();
                List<GoodsStoreInfo> updateList = new ArrayList<>();

                // 遍历 selectedStores
                for (String storeId : goodInfoVO.getSelectedStores()) {
                        // 根据 goodsCode 和 storeId 查找记录
                        LambdaQueryWrapper<GoodsStoreInfo> queryWrapper = new LambdaQueryWrapper<GoodsStoreInfo>()
                                .eq(GoodsStoreInfo::getGoodsId, goodInfoVO.getGoodsCode())
                                .eq(GoodsStoreInfo::getStoreId, storeId);
                        GoodsStoreInfo goodsStoreInfo = goodsStoreInfoService.getOne(queryWrapper);

                        if (goodsStoreInfo == null) {
                                // 如果不存在，则执行插入操作
                                goodsStoreInfo = new GoodsStoreInfo();
                                goodsStoreInfo.setGoodsId(goodInfoVO.getGoodsCode());
                                goodsStoreInfo.setStoreId(storeId);
                                insertList.add(goodsStoreInfo);
                        } else {
                                updateList.add(goodsStoreInfo);
                        }

                        // 根据非空字段判断更新哪个值
                        if (goodInfoVO.getPrice() != null) {
                                goodsStoreInfo.setDistributionPrice(goodInfoVO.getPrice());
                        }

                        if (goodInfoVO.getMinShelfQuantity() != null) {
                                goodsStoreInfo.setMinShelfQuantity(goodInfoVO.getMinShelfQuantity());
                        }
                }

                // 批量插入和更新
                if (!insertList.isEmpty()) {
                        goodsStoreInfoService.saveBatch(insertList);
                }
                if (!updateList.isEmpty()) {
                        goodsStoreInfoService.updateBatchById(updateList);
                }

                return AjaxResult.success("操作成功");
        }

        @Override
        public AjaxResult updateGoodsBaseInfo(GoodsInfo goodsInfo) {

                // 检查 fruitLabel 是否为 3(标签无)，如果不是，则返回错误
                if (goodsInfo.getFruitLabel() != null && goodsInfo.getFruitLabel() != 3 && Integer.valueOf(goodsInfo.getType()) != 1) {

                        return AjaxResult.error("设置必上或者选其一的果品不能设置指定分货");
                }

                LambdaUpdateWrapper<GoodsInfo> wrapper = new LambdaUpdateWrapper<GoodsInfo>().eq(GoodsInfo::getGoodsCode, goodsInfo.getGoodsCode());

                int update = goodsInfoMapper.update(goodsInfo, wrapper);

                return update > 0 ? AjaxResult.success("修改成功") : AjaxResult.success("修改失败) ");
        }

        @Override
        @Transactional
        public AjaxResult addRelatedGoods(GoodInfoVO goodInfoVO) {
                List<String> goodsCodes = goodInfoVO.getGoodsCodes();
                // 查询数据库，检查goodsCodes中的果品代码
                List<GoodsInfo> goodsInfos = goodsInfoMapper.findByGoodsCodes(goodsCodes);

                Set<String> set = new HashSet<>();

                // 将数据库中的关联果品字段全部设为null
                for (GoodsInfo goodsInfo : goodsInfos) {
                        if (goodsInfo.getRelatedGoods() != null) {
                                set = stringToList(goodsInfo.getRelatedGoods()).stream().collect(Collectors.toSet());
                        }
                        set.add(goodsInfo.getGoodsCode());

                }

                for (String goodsCode : set) {
                        lambdaUpdate().eq(GoodsInfo::getGoodsCode, goodsCode)
                                .set(GoodsInfo::getFruitLabel, 3)
                                .set(GoodsInfo::getRelatedGoods, null).update();
                }

                if (goodInfoVO.getFruitLabel() == 2) {
                        // 根据前端传入的果品代码进行合并存入到数据库中
                        for (String goodsCode : goodsCodes) {
                                boolean update = lambdaUpdate().eq(GoodsInfo::getGoodsCode, goodsCode)
                                        .set(GoodsInfo::getFruitLabel, goodInfoVO.getFruitLabel())
                                        .set(GoodsInfo::getRelatedGoods, goodsCodes.toString()).update();

                                if (!update) {
                                        return AjaxResult.error("添加失败");
                                }
                        }
                } else {
                        for (String goodsCode : goodsCodes) {
                                boolean update = lambdaUpdate().eq(GoodsInfo::getGoodsCode, goodsCode)
                                        .set(GoodsInfo::getFruitLabel, goodInfoVO.getFruitLabel())
                                        .set(GoodsInfo::getRelatedGoods, null).update();

                                if (!update) {
                                        return AjaxResult.error("添加失败");
                                }

                        }
                }

                return AjaxResult.success("添加标签成功");

        }

        @Override
        public List<DistributeSendR> distributeSendGoofInfos(DistributeSendVO distributeSendVO) {
                return goodsInfoMapper.distributeSendGoofInfos(distributeSendVO);
        }

        public static boolean areListsEqualIgnoringOrder(List<String> list1, List<String> list2) {
                if (list1 == null || list2 == null) {
                        return list1 == list2;
                }
                if (list1.size() != list2.size()) {
                        return false;
                }
                List<String> sortedList1 = new ArrayList<>(list1);
                List<String> sortedList2 = new ArrayList<>(list2);
                Collections.sort(sortedList1);
                Collections.sort(sortedList2);
                return sortedList1.equals(sortedList2);
        }

        private List<String> stringToList(String s) {
                if (s != null) {
                        s = s.replace("[", "").replace("]", "");
                        return Arrays.asList(s.split(",\\s*"));
                }

                return Arrays.asList("");

        }

}




