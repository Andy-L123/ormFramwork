package com.codeup.mybatis.test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MybatisIntroductionTest {
    public static void main(String[] args) throws IOException {
        //获取SqlSessionFactoryBuilder
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //获取SqlSessionFactory
        InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = builder.build(is);
        //获取SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        //执行SQL
        int count=sqlSession.insert("insertCar");

        System.out.println("插入了"+count+"条");
        sqlSession.commit();
    }

}
