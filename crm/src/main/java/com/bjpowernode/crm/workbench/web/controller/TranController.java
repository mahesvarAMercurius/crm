package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServicesFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceimpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if("/workbench/transaction/create.do".equals(path)){
            create(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/transaction/getTranHistoryListById.do".equals(path)){
            getTranHistoryListById(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getChars.do".equals(path)){
            getChars(request,response);
        }
        
    }

    private void getChars(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("统计交易阶段数据");
        TranService ts = (TranService) ServicesFactory.getService(new TranServiceImpl());
        Map<String,Object> map = ts.getChars();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改阶段");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setExpectedDate(expectedDate);
        t.setMoney(money);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        TranService ts = (TranService) ServicesFactory.getService(new TranServiceImpl());
        boolean flag = ts.changeStage(t);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        t.setPossibility(pMap.get(stage));
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",t);
        PrintJson.printJsonObj(response,map);

    }

    private void getTranHistoryListById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行根据交易Id查询交易历史的操作");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServicesFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getTranHistoryListById(id);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        for(TranHistory th : thList){
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易详情");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServicesFactory.getService(new TranServiceImpl());
        Tran t = ts.detail(id);
        String stage = t.getStage();
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行添加交易操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        Tran t = new Tran();
        t.setType(type);
        t.setId(id);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setActivityId(activityId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setSource(source);
        t.setOwner(owner);
        t.setNextContactTime(nextContactTime);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setContactsId(contactsId);
        TranService ts = (TranService) ServicesFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t,customerName);
        if(flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }


    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("模糊查询客户名称（自动补全）");
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServicesFactory.getService(new CustomerServiceimpl());
        List<String> stringList = cs.getListByName(name);
        PrintJson.printJsonObj(response,stringList);
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到创建页面");
        UserService us = (UserService) ServicesFactory.getService(new UserServiceImpl());
        List<User> userList = us.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
