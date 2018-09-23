package com.secskill.demo.dao;

import com.secskill.demo.domain.SecondSkillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 该类的功能描述
 * @author: zyh
 * @date: 2018-9-2
 */
@Mapper
public interface SecondSkillUserDao {


    @Select("select * from sec_skill_user where id = #{id}")
    SecondSkillUser getById(@Param("id")long id);

}
