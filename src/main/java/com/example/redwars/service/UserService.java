package com.example.redwars.service;



import com.example.redwars.mapper.UserMapper;
import com.example.redwars.model.User;
import com.example.redwars.util.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    /**
     * 该模块是注册模块，根据收到的参数用户名和密码，进行注册，密码用md5进行加密
     * @param name
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    @Transactional
    public boolean register(String name , String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        User user = new User();
        user.setName(name);
        //判断用户名是否已经被使用过
        boolean exist = isExist(name);
        if(exist){
            return false;
        }
        //进行md5加密
        Md5 md5 = new Md5();
        String newPassword = md5.EncoderByMd5(password);
        user.setPassword(newPassword);
        user.setMoney(0.0f);
        add(user);
        return true;
    }

    /**
     * 判断改用户名是否已经被使用过
     * @param name
     * @return
     */
    public boolean isExist(String name){
        if(userMapper.selectNumByName(name) > 0){
            return true;
        }
        return false;
    }


    public void add(User user){
        userMapper.insert(user);
    }

    public Integer get(String name , String password){
        return userMapper.selectIdByNameAndPassword(name , password);
    }

    public void update(User user){
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 对用户名和密码进行校对，如果正确，将用户的ID存进session中
     * @param name
     * @param password
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public boolean login(String name , String password , HttpSession session) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Md5 md5 = new Md5();
        String newPassword = md5.EncoderByMd5(password);
        Integer uid = get(name , newPassword);
        if(uid != null){
            session.setAttribute("uid", uid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 可以对当前用户做一个充钱处理
     * @param session
     * @param money
     * @return
     */
    public boolean addMoney(HttpSession session , float money){
        Integer uid = (Integer) session.getAttribute("uid");
        if(uid == null){
            return false;
        }
        return userMapper.updateMoneyById(uid, money) == 1;
    }
    public int updateMoney(int uid , float money){
        return userMapper.updateMoneyById(uid, money);
    }

    public Float getMoney(int uid){
        return userMapper.selectMoneyById(uid);
    }
}
