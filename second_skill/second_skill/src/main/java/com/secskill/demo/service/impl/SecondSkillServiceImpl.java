package com.secskill.demo.service.impl;

import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillOrder;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.redis.SecondSkillKey;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.service.OrderService;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.util.MD5Util;
import com.secskill.demo.util.UUIDUtil;
import com.secskill.demo.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Service
public class SecondSkillServiceImpl implements SecondSkillService{
    private static final Logger logger = LoggerFactory.getLogger(SecondSkillServiceImpl.class);

    private static final int OVER = -1;
    private static final int QUEUING = 0;
    private static char[] ops = new char[] {'+', '-', '*'};

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Override
    public OrderInfo secondSkill(SecondSkillUser user, GoodsVo goodsVo) {
        boolean success = goodsService.reduceStock(goodsVo);
        if(!success){
            //更新缓存已秒杀完毕
            setGoodsOver(goodsVo.getId());
            return null;
        }

        OrderInfo orderInfo = orderService.createOrder(user, goodsVo);
        if(orderInfo == null){
            throw new GlobalException(CodeMsg.SECOND_SKILL_FAIL);
        }

        return orderInfo;
    }

    @Override
    public long getSecondSkillResult(Long userId, long goodsId) {

        SecondSkillOrder order = orderService.getSecondSkillOrderByUserIdAndGoodsId(userId, goodsId);
        if(order != null){
            return order.getOrderId();
        }
        boolean isOver = getGoodsOver(goodsId);
        return isOver ? OVER : QUEUING;
    }

    @Override
    public boolean checkPath(SecondSkillUser user, long goodsId, String path) {
       if(user == null || StringUtils.isEmpty(path)){
           return false;
       }
       String pathOld = redisService.get(SecondSkillKey.SECOND_SKILL_PATH, ""+user.getId() + "_"+ goodsId, String.class);
       return path.equals(pathOld);
    }

    @Override
    public String createSecondSkillPath(SecondSkillUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String path = MD5Util.md5(UUIDUtil.uuid()+"da_huai_dan");
        redisService.set(SecondSkillKey.SECOND_SKILL_PATH, ""+user.getId() + "_"+ goodsId, path);
        return path;
    }

    @Override
    public BufferedImage createVerifyCode(SecondSkillUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码结果存到redis中
        Integer rnd = calc(verifyCode);
        redisService.set(SecondSkillKey.SECOND_SKILL_VERIFY_CODE, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 计算验证码结果
     * @param exp
     * @return
     */
    private Integer calc(String exp){

     try{
         ScriptEngineManager manager = new ScriptEngineManager();
         ScriptEngine engine = manager.getEngineByName("JavaScript");
         return (Integer)engine.eval(exp);
     }catch (Exception e){
        logger.error("计算验证码{}，结果Exception：{}",exp, ExceptionUtils.getStackTrace(e));
        return 0;
     }
    }

    /**
     * 生成验证码 + - * 三种运算
     * @param rdm
     * @return
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(20);
        int num2 = rdm.nextInt(20);
        char op1 = ops[rdm.nextInt(3)];
        return ""+ num1 + op1 + num2 ;

    }

    @Override
    public boolean checkVerifyCode(SecondSkillUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }

        Integer codeOld = redisService.get(SecondSkillKey.SECOND_SKILL_VERIFY_CODE, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(SecondSkillKey.SECOND_SKILL_VERIFY_CODE, user.getId()+","+goodsId);
        return true;

    }

    @Override
    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    /**
     * 更新缓存信息：已秒杀完毕
     * @param goodsId
     */
    private void setGoodsOver(Long goodsId) {
        redisService.set(SecondSkillKey.IS_GOOD_OVER, ""+goodsId, true);
    }

    /**
     * 从缓存获取是否已秒杀完毕
     * @param goodsId
     * @return
     */
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SecondSkillKey.IS_GOOD_OVER, ""+goodsId);
    }
}
