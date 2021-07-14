package com.example.redwars.service;

import com.example.redwars.mapper.RedItemMapper;
import com.example.redwars.model.RedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RedItemService {
    public static final String EXIST = "EXIST";
    public static final String NONE = "NONE";
    public static final String PAST = "PAST";

    @Autowired
    RedItemMapper redItemMapper;
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public void adds(List lri){
        redItemMapper.insertList(lri);
    }

    public List<RedItem> findByRid(int rid){
        return redItemMapper.selectByRid(rid);
    }

    /**
     * 此方法接受从传来的参数红包ID，和红包项价值，从而创建红包项对象，并存储进数据库中。
     * @param rid
     * @param value
     */

    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public RedItem itemEqualGenerate(int rid , float value ) {
        RedItem redItem = new RedItem();
        redItem.setRid(rid);
        redItem.setStatus(EXIST);
        redItem.setValue(value);
        return redItem;
    }
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public void update(RedItem redItem){
        redItemMapper.updateByPrimaryKeySelective(redItem);
    }


    public List<RedItem> findByRidAndStatus(int rid , String s){

        return redItemMapper.selectByRidAndStatus(rid,s);
    }
}
