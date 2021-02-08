package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServicesFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        //获取账号和密码
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将输入的密码转换成MD5加密形式才能查询出来
        loginPwd = MD5Util.getMD5(loginPwd);
        //获取本机ip
        String ip = request.getRemoteAddr();

        //创建代理对象实现业务逻辑
        UserService us = (UserService) ServicesFactory.getService(new UserServiceImpl());
        //在service层处理登录的业务逻辑，如果出现异常直接跳到catch中
        try{
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);
            PrintJson.printJsonFlag(response,true);
        }catch(Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);
        }

    }
}
