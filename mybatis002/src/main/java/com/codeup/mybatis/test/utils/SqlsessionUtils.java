package com.codeup.mybatis.test.utils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class SqlsessionUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        try {
            sqlSessionFactory=builder.build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession(true);
    }
}
