package com.example.redwars.mapper;


import com.example.redwars.model.RedItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.lang.reflect.Array;
import java.util.List;
@Mapper
public interface RedItemMapper {
    //int insert(RedItem record);
    //dwa
//master test
    int updateByPrimaryKeySelective(RedItem record);

    int insertList(@Param("list") List<RedItem> list);

    List<RedItem> selectByRid(Integer rid);

    List<RedItem> selectByRidAndStatus(@Param("rid") Integer rid , @Param("status") String s);
}