package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServicesFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class DicListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        DicService ds = (DicService) ServicesFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = ds.getAll();
        Set<String> set = map.keySet();
        for(String key : set){
            application.setAttribute(key,map.get(key));
        }

        //读取阶段和可能性对应的键值对的属性配置文件
        Map<String,String> pMap = new HashMap<String,String>();
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = rb.getKeys();
        while(e.hasMoreElements()){
            String key = e.nextElement();
            String value = rb.getString(key);
            pMap.put(key,value);
        }
        application.setAttribute("pMap",pMap);
    }


}
