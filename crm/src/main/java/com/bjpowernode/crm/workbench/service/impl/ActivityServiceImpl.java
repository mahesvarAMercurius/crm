package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.exception.CRUDException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public void save(Activity a) throws CRUDException {
        int count = activityDao.save(a);
        if(count != 1){
            throw new CRUDException("添加市场活动失败");
        }
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        int total = activityDao.getTotalByParam(map);
        List<Activity> dataList = activityDao.getActivityListByParam(map);

        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) throws CRUDException {
        boolean flag = true;
        //按照备注表的外键查询对应的记录条数
        int count1 = activityRemarkDao.getTotalByActivityId(ids);
        //根据外键删除备注
        int count2 = activityRemarkDao.deleteByActivityId(ids);

        if(count2 != count1){
            flag = false;
            throw new CRUDException("删除失败");
        }

        //删除市场活动
        int count3 = activityDao.delete(ids);

        if(count3 != ids.length){
            flag = false;
            throw new CRUDException("删除失败");
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserAndActivity(String id) {

        //获取userList
        List<User> userList = userDao.getUserList();
        //获取activity
        Activity activity = activityDao.getById(id);

        //将两者打包到map集合，返回map
        Map<String,Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("activity",activity);
        return map;
    }

    @Override
    public boolean update(Activity a) throws CRUDException {
        boolean flag = true;
        int count = activityDao.update(a);
        if(count != 1){
            flag = false;
            throw new CRUDException("更新失败");
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByActivityId(String activityId) {
        List<ActivityRemark> activityRemarkList = activityRemarkDao.getRemarkListByActivityId(activityId);
        return activityRemarkList;
    }

    @Override
    public boolean deleteRemarkById(String id) throws CRUDException {
        boolean flag = true;
        int count = activityRemarkDao.deleteRemarkById(id);
        if(count != 1){
            flag = false;
            throw new CRUDException("删除失败");
        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) throws CRUDException {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if(count != 1){
            flag = false;
            throw new CRUDException("保存失败");
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.updateRemark(ar);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activityList = activityDao.getActivityListByClueId(clueId);
        return activityList;
    }

    @Override
    public List<Activity> getActivityListByNameAndClueId(Map<String, String> map) {
        List<Activity> activityList = activityDao.getActivityListByNameAndClueId(map);
        return activityList;
    }

    @Override
    public List<Activity> getActivityListByName(String name) {
        List<Activity> activityList = activityDao.getActivityListByName(name);
        return activityList;
    }
}
