package com.secskill.demo.controller;

import com.secskill.demo.access.AccessLimit;
import com.secskill.demo.domain.SecondSkillOrder;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.rabbitmq.MQSender;
import com.secskill.demo.rabbitmq.SecondSkillMessage;
import com.secskill.demo.redis.GoodsKey;
import com.secskill.demo.redis.OrderKey;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.redis.SecondSkillKey;
import com.secskill.demo.result.CodeMsg;
import com.secskill.demo.result.Result;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.service.OrderService;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.service.SecondSkillUserService;
import com.secskill.demo.vo.GoodsVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Controller
@RequestMapping("/secondSkill")
public class SecondSkillController implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SecondSkillController.class);

    /**
     * 排队中
     */
    private static final int QUEUING = 0;

    @Autowired
    SecondSkillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SecondSkillService skillService;

    @Autowired
    MQSender sender;

    private static ConcurrentHashMap<Long, Boolean> localOvermap = new ConcurrentHashMap<>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(CollectionUtils.isEmpty(goodsVoList)){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            //缓存秒杀商品库存数量
            redisService.set(GoodsKey.SECOND_SKILL_STOCK,""+goodsVo.getId(), goodsVo.getStockCount());
            //false：未秒杀完
            localOvermap.put(goodsVo.getId(), false);
        }
    }

    /**
     * 重置秒杀信息
     * @param model
     * @return
     */
    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<Boolean> reset(Model model){
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for(GoodsVo goodsVo : goodsVoList){
            //可秒杀数量,此处demo写死
            goodsVo.setStockCount(10);
            //redis保存秒杀库存量
            redisService.set(GoodsKey.SECOND_SKILL_STOCK, ""+goodsVo.getId(), 10);
            localOvermap.put(goodsVo.getId(), false);
        }
        redisService.delete(OrderKey.SECOND_SKILL_ORDER);
        redisService.delete(SecondSkillKey.IS_GOOD_OVER);
        skillService.reset(goodsVoList);
        return Result.success(true);
    }

    /**
     * 动态接口地址，进行秒杀，异步下单
     * @param model
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @RequestMapping(value="/{path}/skill", method=RequestMethod.POST)
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<Integer> secondSkill(Model model,SecondSkillUser user, @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path) {

        model.addAttribute("user", user);

        boolean checkPath = skillService.checkPath(user, goodsId, path);
        if(!checkPath){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //内存标记，减少redis访问
        boolean isOver = localOvermap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.SECOND_SKILL_OVER);
        }

        //预减库存
        long stockCount = redisService.decr(GoodsKey.SECOND_SKILL_STOCK,""+goodsId);
        if(stockCount < 0){
            localOvermap.put(goodsId, true);
            return Result.error(CodeMsg.SECOND_SKILL_OVER);
        }

        SecondSkillOrder order = orderService.getSecondSkillOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(order != null){
            return  Result.error(CodeMsg.REPEATE_SECOND_SKILL);
        }

        //使用MQ,异步   下单
        SecondSkillMessage message = new SecondSkillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);

        sender.sendSecondSkillMessage(message);

        return Result.success(QUEUING);

    }

    /**
     * 获取秒杀结果
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<Long> secondSkillResult(Model model,SecondSkillUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        long result = skillService.getSecondSkillResult(user.getId(),goodsId);
        return  Result.success(result);

    }

    /**
     * 获取秒杀接口地址
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<String> getSecondSkillPath(SecondSkillUser user, @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
        boolean checkVerifyCode = skillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!checkVerifyCode){
            return Result.error(CodeMsg.VERIFY_CODE_ILLEGAL);
        }
        String path = skillService.createSecondSkillPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<String> getSecondSkillVerifyCod(HttpServletResponse response, SecondSkillUser user,
                                              @RequestParam("goodsId")long goodsId) {

        try(
                OutputStream out = response.getOutputStream();
                )
        {
            BufferedImage image = skillService.createVerifyCode(user, goodsId);
            ImageIO.write(image,"JPEG",out);
            out.flush();
            return null;
        }catch (Exception e){
            logger.error("获取秒杀验证码错误：{}", ExceptionUtils.getStackTrace(e));
            return Result.error(CodeMsg.SECOND_SKILL_FAIL);
        }

    }

}
