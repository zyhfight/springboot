package com.secskill.demo.service.impl;

import com.secskill.demo.dao.GoodsDao;
import com.secskill.demo.domain.SecondSkillGoods;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 商品Service实现类
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    GoodsDao goodsDao;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goods) {
        SecondSkillGoods g = new SecondSkillGoods();
        g.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(g);
        return ret > 0;
    }

    @Override
    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            SecondSkillGoods g = new SecondSkillGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            int cnt = goodsDao.resetStock(g);
            if(cnt <= 0){
                throw new GlobalException(CodeMsg.RESET_FAIL);
            }
        }
    }
}
