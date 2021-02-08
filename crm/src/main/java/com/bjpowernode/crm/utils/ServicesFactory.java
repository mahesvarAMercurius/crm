package com.bjpowernode.crm.utils;

public class ServicesFactory {
    public static Object getService(Object targer){
        return new TransactionInvocationHandler(targer).getProxy();
    }
}
