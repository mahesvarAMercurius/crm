package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.exception.CRUDException;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    void save(Activity a) throws CRUDException;

    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids) throws CRUDException;

    Map<String, Object> getUserAndActivity(String id);

    boolean update(Activity a) throws CRUDException;

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByActivityId(String activityId);

    boolean deleteRemarkById(String id) throws CRUDException;

    boolean saveRemark(ActivityRemark ar) throws CRUDException;

    boolean updateRemark(ActivityRemark ar);

    List<Activity> getActivityListByClueId(String clueId);

    List<Activity> getActivityListByNameAndClueId(Map<String, String> map);

    List<Activity> getActivityListByName(String name);
}
