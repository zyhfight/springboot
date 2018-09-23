package com.secskill.demo.rabbitmq;

import com.secskill.demo.domain.SecondSkillUser;
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
public class SecondSkillMessage {
    private SecondSkillUser user;
    private long goodsId;
}
