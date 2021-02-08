package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceimpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getListByName(String name) {
        List<String> stringList = customerDao.getListByName(name);
        return stringList;
    }
}
