package org.liu.batis.core;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * 类名：工厂构建类
 * 通过builder方法构建工场类，读取配置文件，获取用户所有配置
 */
public class SqlSessionLiuFactoryBuilder {

    //无参构造方法
    public SqlSessionLiuFactoryBuilder() {
    }

    /**
     * 读取配置文件，构造工厂类
     * @param inputStream 配置文件输入流
     * @return
     * @throws DocumentException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public SqlSessionLiuFactory build(InputStream inputStream) throws DocumentException, SQLException, ClassNotFoundException {
        //读入配置文件工具类
        SAXReader reader = new SAXReader();
        //把流读成文档类
        Document document = reader.read(inputStream);
        //获取指定环境默认值
        Element environments = (Element) document.selectSingleNode("/configuration/environments");
        String defaultEnvironment = environments.attributeValue("default");
        //根据指定，获取实际环境
        Element environmentElt=(Element) environments.selectSingleNode("/configuration/environments/environment[@id='"+defaultEnvironment+"']");
        //获取事务类型
        Element transactionManager = environmentElt.element("transactionManager");
        String defaultTransactionManager = transactionManager.attributeValue("type");
        //获取数据源类型
        Element dataSource=environmentElt.element("dataSource");
        String dataSourceType=dataSource.attributeValue("type");
        //获取数据源属性列表
        List<Element> dataSourceElements = dataSource.elements("property");
        dataSourceElements.forEach(element->{
            String name=element.attributeValue("name");
            String value=element.attributeValue("value");
        });
        //获取mappers中mapper列表
        Element mappers = (Element) document.selectSingleNode("/configuration/environments/mappers");
        List<Element> mappersList = mappers.elements("mapper");
        //获取mapper列表中每个mapper的位置
        mappersList.forEach(element->{
            String resource=element.attributeValue("resource");
        });
        //传入数据源和mapper列表
        return new SqlSessionLiuFactory(dataSourceElements,mappersList);
    }
}
