package com.example.redwars.service;

import com.example.redwars.exception.QuantitativeLogicException;
import com.example.redwars.mapper.RedMapper;
import com.example.redwars.model.Red;
import com.example.redwars.model.RedItem;
import com.example.redwars.model.User;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RedService {
    private static ReentrantLock lock = new ReentrantLock();
    public static final String EXIST = "EXIST";
    public static final String NONE = "NONE";
    public static final String PAST = "PAST";
    @Autowired
    private RedMapper redMapper;
    @Autowired
    private RedItemService redItemService;
    @Autowired
    private UserService userService;

    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public void add(Red red){
        redMapper.insert(red);
    }

    public Red getById(int id){
        return redMapper.selectByPrimaryKey(id);
    }

    public Integer getRemainById(int id){
        return redMapper.selectRemainById(id);
    }

    /**
     * 用来判断红包是否过期的方法
     * @param rid
     * @return
     */
    public boolean timeJudge(int rid ){
        Date date = redMapper.selectTimeById(rid);
        //过期的时间定义为一天
        return new Date().getTime() - date.getTime()>= 86400000L;
    }

    public List<Integer> getRidByStatus(String s1 , String s2){
        return redMapper.selectRidByStatus(s1, s2);
    }

    public String getStatusById(int id){
        return redMapper.selectStatusById(id);
    }

    /**
     * 把当前所有的状态为EXIST或者NONE的红包做一次是否过期判断
     */
    public void past(){
        List<Integer> list = getRidByStatus(EXIST,NONE);
        int len = list.size();
        for(int i = 0; i < len; i++){
            pastWork(list.get(i));
        }
    }

    /**
     * 对指定红包进行是否过期判断，如果有错误则回滚
     * @param rid
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public void pastWork(int rid){
        //判断是否过期
        if(timeJudge(rid)){
            //设定过期后判断
            Red red = new Red();
            red.setId(rid);
            red.setStatus(PAST);
            red.setRemain(0);
            List<RedItem> lri = redItemService.findByRid(rid);
            int size = lri.size();
            //获得剩余钱，用于返还给创建用户
            float surplus = 0f;
            for (int i = 0; i < size; i++) {
                RedItem redItem = lri.get(i);
                //对所有的红包项进行状态判定，如果存在EXIST的将状态设置为PAST，并将剩余钱加入surplus中
                if (redItem.getStatus().equals(RedItemService.EXIST)) {
                    surplus += redItem.getValue();
                }
                redItem.setStatus(RedService.PAST);
                redItemService.update(redItem);
            }
            Integer uid = redMapper.selectUidById(rid);
            //对用户状态的一个更新
            userService.updateMoney(uid,surplus);
            update(red);


        }
    }

    /**
     * 对于登陆的用户的红包等额分配功能，可以指定红包大小，以及红包项的数量
     * @param money
     * @param remain
     * @param num
     * @param session
     * @throws Exception
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized void equalGenerate(float money , int remain , int num , HttpSession session )throws Exception{
        Integer uid = (Integer) session.getAttribute("uid");
        float curMoney = userService.getMoney(uid);
        //判断钱是否够
        if(curMoney < money) {
            throw new QuantitativeLogicException("当前用户余额不足");
        }
        //创建红包对象并更新到数据库中
        Red red = new Red();
        red.setCreatedate(new Date());
        red.setMoney(money);
        red.setNum(num);
        red.setRemain(remain);
        red.setStatus(RedService.EXIST);
        red.setUid(uid);
        add(red);
        //throw new Exception("") 会回滚 观察到自增主键加一
        //这里在RedMapper.xml文件中useGeneratedKeys="true" keyProperty="id"这样设置可以做到insert或者update后返回主键值至对象中
        int rid = red.getId();
        //计算出每个红包项的价钱，并产生各个小红包项
        float littleMoney = money/num;
        List<RedItem> lri = new ArrayList<>();
        for(int i = 0 ; i < num ; i++) {
            lri.add(redItemService.itemEqualGenerate(rid, littleMoney));
        }
        //批量增加
        redItemService.adds(lri);
        //更新红包价格
        userService.updateMoney(uid,-money);
    }
    @Transactional
    public void update(Red red){
        redMapper.updateByPrimaryKeySelective(red);
    }

    public void updateRemain(Red red){
        redMapper.updateRemainByPrimaryKeySelective(red);
    }

    /**
     * 当前网页登陆用户可以进行抢红包这样的功能，可以指定抢那个红包
     * @param rid
     * @param uid
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
    public synchronized boolean getRed(int rid , int uid){
        //获取满足条件的小红包,首先判断该用户是否领取过红包，在判断是否有剩余红包。
        if(!redJudge(rid,uid)){
            return false;
        }
        //获取满足条件的红包项
        List<RedItem> lri = redItemService.findByRidAndStatus(rid, RedItemService.EXIST);
        if(lri == null || lri.size() == 0){
            return false;
        }
        //抢红包
        RedItem ri = lri.get(0);
        //对于抢红包的User对象进行状态更新
        userService.updateMoney(uid , ri.getValue());
        //对红包项进行状态更新
        ri.setStatus(RedItemService.NONE);
        ri.setUid(uid);
        redItemService.update(ri);
        //对红包进行状态更新
        Red red = new Red();
        red.setRemain(1);
        //判断是否为空，如果为空，设定状态是none
        if (red.getRemain() == 0) {
            red.setStatus(NONE);
        }
        red.setId(rid);
        updateRemain(red);
        return true;
    }

    /**
     * 进行一个红包判断，主要功能就是判断这个红包是否被当前用户抢过，如果已经抢过，就不能在抢
     * @param rid
     * @param uid
     * @return
     */
    public boolean redJudge(int rid , int uid){
        List<RedItem> lri = redItemService.findByRid(rid);
        for (RedItem redItem : lri) {
            if (redItem.getUid() == uid) {
                return false;
            }
        }
        return true;
    }

    public boolean isPastOrNone(int rid){
        String s = getStatusById(rid);
        return PAST.equals(s) || NONE.equals(s);
    }

}
