package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServicesFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/activity/getRemarkListByActivityId.do".equals(path)){
            getRemarkListByActivityId(request,response);
        }else if("/workbench/activity/deleteRemarkById.do".equals(path)){
            deleteRemarkById(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }


    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入修改市场活动备注信息的操作");
        String noteContent = request.getParameter("noteContent");
        String id = request.getParameter("id");
        String editTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        String editFlag = "1";
        ActivityRemark ar = new ActivityRemark();
        ar.setEditFlag(editFlag);
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入添加市场活动备注信息的操作");
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        String editFlag = "0";
        ActivityRemark ar = new ActivityRemark();
        ar.setActivityId(activityId);
        ar.setNoteContent(noteContent);
        ar.setId(id);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        try {
            boolean flag = as.saveRemark(ar);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",flag);
            map.put("ar",ar);
            PrintJson.printJsonObj(response,map);
        }catch (Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);
        }


    }

    private void deleteRemarkById(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动删除的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        try{
            boolean flag = as.deleteRemarkById(id);
            PrintJson.printJsonFlag(response,flag);
        }catch (Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);
        }

    }

    private void getRemarkListByActivityId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入查询市场活动备注列表的操作");
        String activityId = request.getParameter("activityId");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> activityRemarkList = as.getRemarkListByActivityId(activityId);
        PrintJson.printJsonObj(response,activityRemarkList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入查询市场活动详情操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        Activity a = as.detail(id);
        request.setAttribute("a",a);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);

    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动更新操作");
        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String editBy = user.getName();
        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditTime(editTime);
        a.setEditBy(editBy);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        try{
            boolean flag = as.update(a);
            PrintJson.printJsonFlag(response,flag);
        }catch(Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
        }


    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入按id查询市场活动信息的操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = as.getUserAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动的删除操作");
        String ids[] = request.getParameterValues("id");
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        boolean flag;
        try {
            flag = as.delete(ids);
            PrintJson.printJsonFlag(response,flag);
        }catch (Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);

        }

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动的条件查询和分页操作");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        Integer pageNo = Integer.valueOf(request.getParameter("pageNo"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        PaginationVO<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入市场活动添加的操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        User user = (User) request.getSession().getAttribute("user");
        String createBy = user.getName();
        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);
        ActivityService as = (ActivityService) ServicesFactory.getService(new ActivityServiceImpl());
        try{
            as.save(a);
            PrintJson.printJsonFlag(response,true);
        }catch(Exception e){
            e.printStackTrace();
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
        }


    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入获取用户列表的操作");
        UserService us = (UserService) ServicesFactory.getService(new UserServiceImpl());
        List<User> userList = us.getUserList();
        PrintJson.printJsonObj(response,userList);
    }
}
