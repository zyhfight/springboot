package com.secskill.demo.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Setter
@Getter
@ToString
public class SecondSkillUser {

    private Long id;
    private String nickName;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;

}
