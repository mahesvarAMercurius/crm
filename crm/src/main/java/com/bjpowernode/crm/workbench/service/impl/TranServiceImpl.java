package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag = true;
        //通过客户名称查询，判断是否存在，没有就创建
        Customer c = customerDao.getByName(customerName);
        if(c == null){
            c = new Customer();
            c.setId(UUIDUtil.getUUID());
            c.setCreateBy(t.getCreateBy());
            c.setCreateTime(DateTimeUtil.getSysTime());
            c.setName(customerName);
            c.setOwner(t.getOwner());
            c.setNextContactTime(t.getNextContactTime());
            c.setDescription(t.getDescription());
            c.setContactSummary(t.getContactSummary());
            int count1 = customerDao.save(c);
            if(count1 != 1){
                flag = false;
            }
        }
        //创建交易
        t.setCustomerId(c.getId());
        int count2 = tranDao.save(t);
        if(count2 != 1){
            flag = false;
        }
        //创建交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getCreateBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        int count3 = tranHistoryDao.save(th);
        if(count3 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t = tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> getTranHistoryListById(String id) {
        List<TranHistory> thList = tranHistoryDao.getTranHistoryListById(id);
        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        int count1 = tranDao.changeStage(t);
        if(count1 != 1){
            flag = false;
        }
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        th.setTranId(t.getId());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        int count2 = tranHistoryDao.save(th);
        if(count2 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getChars() {
        //总记录条数
        int total = tranDao.getTotal();
        //dataList
        List<Map<String,Object>> dataList = tranDao.getChars();
        //打包Map
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
