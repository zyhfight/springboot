package com.secskill.demo.vo;

import com.secskill.demo.domain.Goods;
import com.secskill.demo.domain.SecondSkillUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class GoodsVo extends Goods {
	private Double secSkillPrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;

}
