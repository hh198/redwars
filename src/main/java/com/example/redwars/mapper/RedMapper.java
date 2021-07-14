package com.example.redwars.mapper;


import com.example.redwars.model.Red;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
@Mapper
public interface RedMapper {

    int insert(Red record);

    Red selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Red record);


    Date selectTimeById(Integer id);

    Integer selectUidById(Integer id);

    Integer selectRemainById(Integer id);

    List<Integer> selectRidByStatus(@Param("status1") String s1 , @Param("status2") String s2);

    String selectStatusById(Integer id);

    int updateRemainByPrimaryKeySelective(Red red);
}