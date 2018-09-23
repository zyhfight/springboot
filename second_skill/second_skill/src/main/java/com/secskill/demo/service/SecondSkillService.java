package com.secskill.demo.service;

import com.secskill.demo.domain.OrderInfo;
import com.secskill.demo.domain.SecondSkillUser;
import com.secskill.demo.exception.GlobalException;
import com.secskill.demo.vo.GoodsVo;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
public interface SecondSkillService {

    /**
     * 秒杀
     * @param user
     * @param goodsVo
     * @return OrderInfo
     */
    @Transactional(rollbackFor = GlobalException.class)
    OrderInfo secondSkill(SecondSkillUser user, GoodsVo goodsVo);

    /**
     * 获取秒杀结果
     * @param userId
     * @param goodsId
     * @return -1：秒杀结束；0：排队秒杀中 else：秒杀成功
     */
    long getSecondSkillResult(Long userId, long goodsId);

    /**
     * 校验秒杀path
     * @param user
     * @param goodsId
     * @param path
     * @return boolean
     */
    boolean checkPath(SecondSkillUser user, long goodsId, String path);

    /**
     * 创建秒杀path
     * @param user
     * @param goodsId
     * @return String
     */
    String createSecondSkillPath(SecondSkillUser user, long goodsId);

    /**
     * 创建秒杀验证码
     * @param user
     * @param goodsId
     * @return BufferedImage
     */
     BufferedImage createVerifyCode(SecondSkillUser user, long goodsId);

    /**
     * 校验秒杀验证码
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return boolean
     */
     boolean checkVerifyCode(SecondSkillUser user, long goodsId, int verifyCode);

    /**
     * 重置秒杀信息
     * @param goodsList
     */
    @Transactional(rollbackFor = GlobalException.class)
     void reset(List<GoodsVo> goodsList);

}
