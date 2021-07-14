package com.example.redwars.web;

import com.example.redwars.model.Red;
import com.example.redwars.model.User;
import com.example.redwars.service.RedItemService;
import com.example.redwars.service.RedService;
import com.example.redwars.service.UserService;
import com.example.redwars.util.Result;
import com.github.pagehelper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import javax.swing.*;


@RestController
public class ForeController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedService redService;
    @Autowired
    private RedItemService redItemService;

    /**
     * 这是项目的注册接口，将用户的密码进行md5加密，并存储进数据库
     * 用户的金钱属性初始为0；
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Object register(@RequestBody User user) {

        if(user == null){
            return Result.fail("请传入对象");
        }
        String name = user.getName();
        String password = user.getPassword();
        if(name == null || password == null ||name.equals("") || password.equals("")) {
            return Result.fail("用户和密码都不许为空");
        }

        try {
            if (!userService.register(name, password)) {
                return Result.fail("用户注册失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("用户注册失败");
        }
        return Result.success();
    }

    /**
     * 这是用户的登陆接口
     * 在验证密码正确以后，将用户的id放进Session中
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Object login(@RequestBody User user, HttpSession session) {
        if(user == null){
            return Result.fail("需传入对象");
        }
        String name = user.getName();
        String password = user.getPassword();
        if(name == null || password == null || name.equals("") || password.equals("")) {
            return Result.fail("用户和密码都不许为空");
        }
        try {
            if (!userService.login(name, password, session)) {
                return Result.fail("登陆失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("登陆失败");
        }
        return Result.success();
    }

    /**
     * 这是用户的退出接口
     * 将id从session中取出
     * 并且跳转到登陆页面
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("uid");
        return "redirect:login";
    }

    /**
     * 这是用户的充钱接口
     * 根据session中的id来判断是哪一个用户需要充值
     * 其他充值相关属性有待开发
     * @param money
     * @param session
     * @return
     */
    @PostMapping("/recharge")
    public Object recharge(@RequestParam(value = "money") float money, HttpSession session){
        if(money <= 0){
            return Result.fail("充值数目需要大于零，请重新输入");
        }
        if(!userService.addMoney(session, money)){
            return Result.fail("充值失败");
        }
        return Result.success();
    }

    /**
     * 这是项目的发等额红包接口
     * 用户可以指定红包总大小和内含数目
     * @param money
     * @param num
     * @param session
     * @return
     */
    @PostMapping("/redequal")
    public Object redEqualGenerate(@RequestParam(value = "money") float money, @RequestParam(value = "num" ) int num , HttpSession session) throws Exception {
        if(money <= 0){
            return Result.fail("分配的钱需要大于零，请重新输入");
        }
        if(num <= 0){
            return Result.fail("分配红包数目需要大于零，请重新输入");
        }

        redService.equalGenerate(money,num,num,session);

        return Result.success();
    }

    /**
     * 这是用户的抢红包接口
     * 一个用户可以抢任意一个红包，且仅可以获取一次
     * 在抢红包之前会进行一个红包是否过期判定
     * @param session
     * @param rid
     * @return
     */
    @PostMapping("/redreceive")
    public Object redReceive(HttpSession session, @RequestParam(value = "rid") int rid){
        Integer uid = (Integer) session.getAttribute("uid");
        if(uid == null) {
            String message ="用户没登录";
            return Result.fail(message);
        }
        if(redService.isPastOrNone(rid)) {
            return Result.fail("该红包已空或者过期");
        }
        if (!redService.getRed(rid, uid)) {
            return Result.fail("领取失败");
        }
        return Result.success("红包领取成功");
    }
}
