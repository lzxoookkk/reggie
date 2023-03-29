package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER  = new AntPathMatcher();
//    @Autowired
//    private EmployeeService employeeService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        //1.获取本次url地址
            String url = httpServletRequest.getRequestURI();
            log.info("拦截到请求：{}",url);
            String[] urls = new String[]{
                    "/employee/login",
                    "/employee/logout",
                    "/backend/**",
                    "/front/**",
                    "/user/sendMsg",
                    "/user/login"
            };
        //2.判断本次请求是否要处理
        boolean check = check(urls, url);

        //3.如果不用处理，则放行
        if (check){
            log.info("本次请求{}不需要处理",url);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }

        //4-1.判断登录状态，如已登录，则放行
        if (httpServletRequest.getSession().getAttribute("employee") != null){
            log.info("用户已登录。id为{}",httpServletRequest.getSession().getAttribute("employee"));

            Long empId = (Long) httpServletRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            log.info("id{}",empId);

            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //4-2.判断登录状态，如已登录，则放行
        if (httpServletRequest.getSession().getAttribute("user") != null){
            log.info("用户已登录。id为{}",httpServletRequest.getSession().getAttribute("user"));

            Long userId = (Long) httpServletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            log.info("id{}",userId);

            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }


        //5.如果未登录则返回未登录结果
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check(String[] urls,String requestURL){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match){
                return true;
            }
        }
        return false;
    }

}
