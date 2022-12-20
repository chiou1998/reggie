package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping //添加员工账户并保存
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

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造分页构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long employee1 = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(employee1);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null) {
            return R.success(employee);
        }
        return R.error("没有查询到该员工的信息");
    }
}
