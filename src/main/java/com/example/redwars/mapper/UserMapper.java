package com.example.redwars.mapper;


import com.example.redwars.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface UserMapper {

    int insert(User record);

    int updateByPrimaryKeySelective(User record);

    Integer selectIdByNameAndPassword(@Param("name")String name,@Param("password")String password);

    User selectIdByName(@Param("name")String name);

    Float selectMoneyById(Integer id);

    int updateMoneyById(@Param("id") Integer id, @Param("money") Float money);

    Integer selectNumByName(String name);
}