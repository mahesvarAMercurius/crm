package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user = userDao.login(map);

        //验证账户密码是否存在
        if(null == user){
            throw new LoginException("用户名或密码错误");
        }

        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(currentTime) < 0){
            throw new LoginException("用户已失效");
        }

        //验证锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new LoginException("用户已锁定");
        }

        //验证IP地址是否有效
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new LoginException("用户ip受限");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.getUserList();
        return userList;
    }
}
