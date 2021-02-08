package com.bjpowernode.crm.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionUtil {
    private SqlSessionUtil(){}
    private static SqlSessionFactory sqlSessionFactory;
    private static ThreadLocal<SqlSession> t = new ThreadLocal<SqlSession>();
    static{
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
    public static SqlSession getSqlSession(){
        SqlSession sqlSession = t.get();
        if(sqlSession == null){
            sqlSession = sqlSessionFactory.openSession();
            t.set(sqlSession);
        }
        return sqlSession;
    }
    public static void closes(SqlSession sqlSession){
        if(sqlSession != null){
            sqlSession.close();
            t.remove();
        }
    }
}