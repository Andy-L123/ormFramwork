package com.codeup.mybatis.test;

import com.codeup.mybatis.test.utils.SqlsessionUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;

public class test {

    @Test
    public void testInsert() {
        SqlSession sqlSession = null;
        try {
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = builder.build(Resources.getResourceAsStream("mybatis-config.xml"));
            //开启会话
            sqlSession=sqlSessionFactory.openSession();
            //执行，处理业务
            int count=sqlSession.insert("insertCar");
            System.out.println(count);
            //提交事务
            sqlSession.commit();
        } catch (IOException e) {
            if (sqlSession != null){
                sqlSession.rollback();
            }
            e.printStackTrace();
        }finally {
            if (sqlSession != null) {
                sqlSession.close();
            }

        }

    }
    @Test
    public void testInsertCar() {
       SqlSession sqlSession = SqlsessionUtils.openSession();
        int count=sqlSession.insert("insertCar");
        System.out.println("插入了几条记录"+count);
        sqlSession.close();
    }
}
