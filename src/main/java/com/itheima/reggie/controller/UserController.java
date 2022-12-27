package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        //获取手机号
        String phone = user.getPhone();
         String phone1 = "17679246530";
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证吗
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云的短信服务api完成发送短信

            SMSUtils.sendMessage("瑞吉外卖","SMS_265605163",phone,code);
            //将生成的验证码保存到session
            //session.setAttribute(phone,code);

            redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);

            return R.success("验证码发送成功123");
        }
        return R.error("验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //验证码比对
        if (codeInSession !=null && codeInSession.equals(code)) {
            //对比成功denglu

            //判断当前手机号是否为新用户，如果是新用户就完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                 user = new User();
                 user.setPhone(phone);
                 user.setStatus(1);
                 userService.save(user);

            }
            session.setAttribute("user",user.getId());
            //如果登录成功删除redis中缓存的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
