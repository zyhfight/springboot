package com.secskill.demo.vo;

import com.secskill.demo.domain.SecondSkillUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GoodsDetailVo {
	private int secSkillStatus = 0;
	private int remainSeconds = 0;
	private GoodsVo goods ;
	private SecondSkillUser user;

}
