package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
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
public class EmployeeController{

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1.将页面提交的密码进行M5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到返回失败结果
        if (emp == null){
            return R.error("登录失败");
        }

        //4.如果密码错误返回失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5.如果为禁用状态则返回禁用结果
        if (emp.getStatus() == 0){
            return R.error("账户已禁用");
        }

        //6.登录成功，将员工id存入session并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //获取登录时设置的用户id，清除数据
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //获取登录时设置的用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //设置创建人
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //获取page对象
        Page<Employee> pageInfo = new Page<Employee>(page,pageSize);
        //给分页设置条件
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lqw.orderByDesc(Employee::getId);

        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    //修改数据and启用禁用
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        //获取登录时设置的用户id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        //判断是不是管理员在修改
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    //回显数据
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        //获取id用户数据
        Employee employee = employeeService.getById(id);

        if(employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
