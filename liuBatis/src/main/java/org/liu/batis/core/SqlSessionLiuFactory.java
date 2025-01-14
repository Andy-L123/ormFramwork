package org.liu.batis.core;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.util.List;

/**
 * 工厂类：对应一个数据库
 * 作用：初始化并链接数据库
 */
public class SqlSessionLiuFactory {
    private String url = "";
    private String username = "";
    private String password = "";
    private String driver = "";
    private Connection conn;
    private PreparedStatement pst;
    private List<Element> mappersList;
    public SqlSessionLiuFactory() {

    }

    /**
     * 有参构造方法，jdbc连接数据库并获取conn对象
     * @param dataSourceElements 数据源参数
     * @param mappersList mapper文件映射列表
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public SqlSessionLiuFactory(List<Element> dataSourceElements,List<Element> mappersList) throws SQLException, ClassNotFoundException {
        this.mappersList=mappersList;
        dataSourceElements.forEach(element -> {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            if("url".equals(name)) {
                this.url = value;
            }
            if("username".equals(name)) {
                this.username = value;
            }
            if("password".equals(name)) {
                this.password = value;
            }
            if("driver".equals(name)) {
                this.driver = value;
            }
        });
        //构造结束初始化
        this.jdbcInit();
    }

    /**
     * 加载驱动、初始化数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void jdbcInit() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        this.conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * 建立数据库链接
     * @return
     * @throws ClassNotFoundException
     */
    public SqlSessionLiu openSession() throws ClassNotFoundException {
        return new SqlSessionLiu(conn,mappersList);
    }



}
