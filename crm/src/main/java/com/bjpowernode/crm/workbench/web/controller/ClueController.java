package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServicesFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/clue/getActivityListByNameAndClueId.do".equals(path)){
            getActivityListByNameAndClueId(request,response);
        }else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }
        
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("线索转换");
        String clueId = request.getParameter("clueId");
        System.out.println(clueId);
        String flag = request.getParameter("flag");
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        Tran t = null;
        if("1".equals(flag)){//说明创建交易
            t = new Tran();
            t.setId(UUIDUtil.getUUID());
            t.setMoney(request.getParameter("money"));
            t.setName(request.getParameter("name"));
            t.setExpectedDate(request.getParameter("expectedDate"));
            t.setStage(request.getParameter("stage"));
            t.setActivityId(request.getParameter("activityId"));
            String createTime = DateTimeUtil.getSysTime();

            t.setCreateTime(createTime);
            t.setCreateBy(createBy);
        }
        ClueService cs = (ClueService) ServicesFactory.getService(new ClueServiceImpl());
        boolean tab = cs.convert(clueId,t,createBy);
        if(tab){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("搜索市场活动源");
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.getActivityListByName(name);
        PrintJson.printJsonObj(response,activityList);

    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("关联市场活动");
        String clueId = request.getParameter("clueId");
        String activityIds[] = request.getParameterValues("activityId");
        ClueService cs = (ClueService) ServicesFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(clueId,activityIds);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getActivityListByNameAndClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("关联市场活动（模糊查询）");
        String activityname = request.getParameter("activityname");
        String clueId = request.getParameter("clueId");
        Map<String,String> map = new HashMap<String,String>();
        map.put("activityname",activityname);
        map.put("clueId",clueId);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.getActivityListByNameAndClueId(map);
        PrintJson.printJsonObj(response,activityList);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("解除关联");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServicesFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展现关联市场活动");
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        List<Activity> activityList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,activityList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索备注");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServicesFactory.getService(new ClueServiceImpl());
        Clue c = cs.detail(id);
        request.setAttribute("c",c);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入添加线索");
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");
        Clue c = new Clue();
        c.setWebsite(website);
        c.setState(state);
        c.setSource(source);
        c.setPhone(phone);
        c.setOwner(owner);
        c.setNextContactTime(nextContactTime);
        c.setMphone(mphone);
        c.setJob(job);
        c.setId(id);
        c.setFullname(fullname);
        c.setEmail(email);
        c.setDescription(description);
        c.setCreateTime(createTime);
        c.setCreateBy(createBy);
        c.setContactSummary(contactSummary);
        c.setCompany(company);
        c.setAppellation(appellation);
        c.setAddress(address);
        ClueService cs = (ClueService) ServicesFactory.getService(new ClueServiceImpl());
        boolean flag = cs.save(c);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入查询所有者的操作");
        UserService us = (UserService) ServicesFactory.getService(new UserServiceImpl());
        List<User> userList = us.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
