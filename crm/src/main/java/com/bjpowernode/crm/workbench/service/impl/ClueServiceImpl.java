package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.UUID;

public class ClueServiceImpl implements ClueService {

    //线索相关的表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关的表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关的表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao= SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao= SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关的表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao= SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue c) {
        boolean flag = true;
        int count = clueDao.save(c);
        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c = clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueDao.unbund(id);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bund(String clueId, String[] activityIds) {
        boolean flag = true;
        for(String aid : activityIds){
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(aid);
            int count = clueActivityRelationDao.bund(car);
            if(count != 1){
                flag = false;
            }

        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag = true;
        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getById(clueId);
        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer = customerDao.getByName(company);
        if(customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            customer.setAddress(clue.getAddress());
            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }
        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(DateTimeUtil.getSysTime());
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());
        int count2 = contactsDao.save(contacts);
        if(count2 != 1){
            flag = false;
        }
        //(4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for(ClueRemark clueRemark : clueRemarkList){
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(clueRemark.getNoteContent());
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setCreateTime(DateTimeUtil.getSysTime());
            customerRemark.setCreateBy(createBy);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){
                flag = false;
            }
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(clueRemark.getNoteContent());
            contactsRemark.setEditFlag("0");
            contactsRemark.setCreateTime(DateTimeUtil.getSysTime());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(contacts.getId());
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
        }
        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(clueActivityRelation.getActivityId());
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }
        //(6) 如果有创建交易需求，创建一条交易
        if(t != null){
            t.setSource(clue.getSource());
            t.setOwner(clue.getOwner());
            t.setNextContactTime(clue.getNextContactTime());
            t.setDescription(clue.getDescription());
            t.setCustomerId(customer.getId());
            t.setContactSummary(clue.getContactSummary());
            t.setContactsId(contacts.getId());
            int count6 = tranDao.save(t);
            if(count6 != 1){
                flag = false;
            }
            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setTranId(t.getId());
            tranHistory.setStage(t.getStage());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setCreateTime(t.getCreateTime());
            tranHistory.setCreateBy(t.getCreateBy());
            int count7 = tranHistoryDao.save(tranHistory);
            if(count7 != 1){
                flag = false;
            }
        }

        //(8) 删除线索备注
        for(ClueRemark clueRemark : clueRemarkList){
            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8 != 1){
                flag = false;
            }
        }
        //(9) 删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9 != 1){
                flag = false;
            }
        }
        //(10) 删除线索
        int count10 = clueDao.delete(clueId);
        if(count10 != 1){
            flag = false;
        }
        return flag;
    }
}
