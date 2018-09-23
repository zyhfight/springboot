package com.secskill.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Setter
@Getter
@ToString
public class SecondSkillOrder {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;
}
