package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.service.EmployeeService;




import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")//员工登录接口
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1 将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2 更具页面提交的用户名来查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3 如果没有查询到则返回登陆失败的结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4 密码比对 如果不一致则返回登陆失败的结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // 5 查询员工状态是否禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6 登录成功 将员工的id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")//员工登出接口
    public R<String> logout(HttpServletRequest request){
        // 清理session中保存的员工的id
        request.getSession().removeAttribute("employee");

        return R.success("等出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){

        //设置初始密码123456需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
}
