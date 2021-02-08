package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    int getTotalByActivityId(String[] ids);

    int deleteByActivityId(String[] ids);

    List<ActivityRemark> getRemarkListByActivityId(String activityId);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    int updateRemark(ActivityRemark ar);
}
