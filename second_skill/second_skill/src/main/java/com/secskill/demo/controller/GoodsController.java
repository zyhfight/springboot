package com.secskill.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.secskill.demo.access.AccessLimit;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.redis.GoodsKey;
import com.secskill.demo.redis.RedisService;
import com.secskill.demo.result.Result;
import com.secskill.demo.service.GoodsService;
import com.secskill.demo.service.SecondSkillService;
import com.secskill.demo.service.SecondSkillUserService;
import com.secskill.demo.vo.GoodsDetailVo;
import com.secskill.demo.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);
    /**
     * 秒杀还未开始
     */
    private static final int NO_START = 0;
    /**
     * 秒杀进行中
     */
    private static final int STARTING = 1;
    /**
     * 秒杀已结束
     */
    private static final int OVER = 2;

    @Autowired
    SecondSkillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 页面缓存 获取商品列表页
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value="/list2", produces="text/html")
    @ResponseBody
    public String list2(HttpServletRequest request, HttpServletResponse response, Model model, SecondSkillUser user){

        model.addAttribute("user",user);

        //取页面缓存；如果有分页的话，可以考虑缓存前3页商品list；
        String html = redisService.get(GoodsKey.GOOD_LIST, "",String.class);
        if (StringUtils.isNotEmpty(html)){
            return html;
        }

        //手动渲染页面
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);

        //进行页面缓存
        if(StringUtils.isNotEmpty(html)){
            redisService.set(GoodsKey.GOOD_LIST,"",html);
        }
        return html;
    }

    /**
     * 商品list 缓存
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value="/list")
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<List<GoodsVo>> list(HttpServletRequest request, HttpServletResponse response, Model model, SecondSkillUser user){
        //从redis中获取，可以放到goodsService中执行
        String goodsListStr = redisService.get(GoodsKey.GOOD_LIST,"", String.class);
        if(StringUtils.isEmpty(goodsListStr)){
            List<GoodsVo> goodsList = goodsService.listGoodsVo();
            goodsListStr = JSON.toJSONString(goodsList);
            redisService.set(GoodsKey.GOOD_LIST, "",goodsListStr);
        }

        List<GoodsVo> goodsList = JSONObject.parseArray(goodsListStr,GoodsVo.class);
        return Result.success(goodsList);

    }

    /**
     * url缓存 获取详情页
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,SecondSkillUser user,
                         @PathVariable("goodsId")long goodsId){

        logger.info("用户：{}，查看商品ID：{} 明细", user, goodsId);

        model.addAttribute("user",user);

        //取url缓存
        String html = redisService.get(GoodsKey.GOOD_DETAIL,""+goodsId,String.class);
        if(StringUtils.isNotEmpty(html)){
            return html;
        }
        //手动渲染
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);
        chooseSecondSkill(goodsVo,model);

        SpringWebContext ctx = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);

        //进行url缓存
        if(StringUtils.isNotEmpty(html)){
            redisService.set(GoodsKey.GOOD_DETAIL,""+goodsId, html);
        }
        return html;
    }

    /**
     * 对象缓存 获取详情页
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 20, needLogin = true)
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, SecondSkillUser user,
                                        @PathVariable("goodsId")long goodsId) {

        GoodsVo goodsVo = redisService.get(GoodsKey.GOOD_DETAIL, ""+goodsId, GoodsVo.class);
        if(goodsVo == null){
            goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
            redisService.set(GoodsKey.GOOD_DETAIL, ""+goodsId, goodsVo);
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goodsVo);
        vo.setUser(user);
        vo = dealSecondSkill(goodsVo, vo);
        return Result.success(vo);
    }


    /**
     * 将秒杀状态、剩余秒数set到GoodsDetailVo
     * @param goodsVo
     * @param vo
     * @return
     */
    private GoodsDetailVo dealSecondSkill(GoodsVo goodsVo, GoodsDetailVo vo){

        int secSkillStatus = NO_START;
        int remainSeconds = 0;

        if(goodsVo == null){
            vo.setRemainSeconds(remainSeconds);
            vo.setSecSkillStatus(secSkillStatus);
            return vo;
        }

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        if(now < startTime ) {//秒杀还没开始，倒计时
            secSkillStatus = NO_START;
            remainSeconds = (int)((startTime - now )/1000);
        }else  if(now > endTime){//秒杀已经结束
            secSkillStatus = OVER;
            remainSeconds = -1;
        }else {//秒杀进行中
            secSkillStatus = STARTING;
            remainSeconds = 0;
        }

        vo.setRemainSeconds(remainSeconds);
        vo.setSecSkillStatus(secSkillStatus);

        return vo;
    }

    /**
     * 判断秒杀活动状态和还剩多少秒开始
     * @param goods
     * @param model
     */
    private void chooseSecondSkill(GoodsVo goods,Model model){
        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();
        int secondSkillStatus = NO_START;
        int remainSeconds = 0;
        if (currentTime < startTime) {
            remainSeconds = (int)((startTime - currentTime) / 1000);
        }else if (currentTime > endTime) {
            secondSkillStatus = OVER;
            remainSeconds = -1;
        }else {
            secondSkillStatus = STARTING;
            remainSeconds = 0;
        }
        model.addAttribute("secondSkillStatus", secondSkillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

    }

}
