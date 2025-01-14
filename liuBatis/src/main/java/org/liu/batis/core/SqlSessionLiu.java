package org.liu.batis.core;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.liu.batis.utils.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSessionLiu {
    private Connection conn;
    private PreparedStatement pst;
    private ResultSet resultSet;
    private List<Element> mappersName;
    private List<Map<String,Object>> sqlList=new ArrayList<>();
    private List<String> seqArray = new ArrayList<>();
    private String resultType;

    //无参构造
    public SqlSessionLiu() {}

    /**
     *有参构造
     * @param conn 数据库连接
     * @param mappersNameList mapper文件列表
     */
    public SqlSessionLiu(Connection conn, List<Element> mappersNameList)  {
        this.mappersName = mappersNameList;
        this.conn = conn;
        this.init();//遍历解析mapper.xml文件内容处理成List
    }

    /**
     * 初始化xml功能：
     * 1.解析xml中的命名空间加sql语句
     * 2.形成(key,value)的map集合，(key:namespace,value:命名空间+id)，(key:sqlValue,value：sql语句).
     * 3.如果有resultType存在map中增加，kv，（key：resultType，value:类型String）
     * 4.定义hashmap，记录sql中传参的顺序
     */
    public void init() {
        //遍历处理解析每一个mapper文件
        mappersName.forEach(mapper->{
            try {
                //获取每个mapper的类路径位置
                String resource = mapper.attributeValue("resource");
                //xml文件读取类
                SAXReader reader = new SAXReader();
                Document xmlDocument = reader.read(Resource.getResourceAsStream(resource));
                //获取根标签
                Element rootMapper = xmlDocument.getRootElement();
                //从根标签获取命名空间
                String namespace = rootMapper.attributeValue("namespace");
                //获取所有子标签，并构造List
                List<Element> elements = rootMapper.elements();

                elements.forEach(element -> {
                    //获取SQL操作标签名
                    String name = element.getName();
                    //获取SQL操作方法ID
                    String id = element.attributeValue("id");
                    //获取标签内容
                    String sql = element.getTextTrim();
                    //提取sql传参顺序
                    List<String> seqArray = ExtractPlaceholders(sql);
                    //正则处理标签成jdbc可处理的
                    String Sql = sql.replaceAll("#\\{[0-9A-Za-z_$]*}", "?");
                    //放入map
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("nameSpaceAndFunction", namespace + "." + id);
                    tempMap.put("function", id);
                    tempMap.put("sqlValue", Sql);
                    tempMap.put("seq", seqArray);
                    //如果是查询还需要放入resultType
                    if ("select".equals(name)) {
                        String resultType = element.attributeValue("resultType");
                        tempMap.put("resultType", resultType);
                    }
                    //map添加到list
                    this.sqlList.add(tempMap);
                });
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 提取sql语句中的顺序
     * @param sql 正则？替换前sql
     * @return SQL参数顺序List
     */
    public List<String> ExtractPlaceholders(String sql) {
        // 正则表达式用于匹配 #{}
        Pattern pattern = Pattern.compile("\\#\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(sql);
        List<String> placeholders = new ArrayList<>();
        while (matcher.find()) {
            // 提取并添加到List中
            String placeholder = matcher.group(1); // 获取 #{ 和 } 之间的内容
            placeholders.add(placeholder);
        }
        return placeholders;
    }

    /**
     * 将字符串首字母大写的方法
     * @param str
     * @return
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 插入语句
     * @param sqlName
     * @param paramObject
     * @return
     * @throws SQLException
     */
    public int Insert(String sqlName,Object paramObject) throws Exception {
        String Sql=this.findSql("insert", sqlName);
        if (Sql == null) {
            throw new Exception("mapper中不存在调用放法");
        }
        //预编译sql语句
        pst = conn.prepareStatement(Sql);
        //sql语句参数设置(用户传pojo对象 or 用户传map有序无需对象)
        //用户传LinkedHashMap有序对象，来保持插入顺序
        if (paramObject instanceof Map){//判断是Map
            int i = 0;// 假设这是你要开始设置参数的位置索引-1
            // 按照插入顺序取出所有的值并设置到 PreparedStatement 中
            for(int j=0;j<seqArray.size();j++){
                Object value = ((Map<?, ?>) paramObject).get(seqArray.get(j));
                pst.setObject(++i, value);
            }
        }else {//如果是实体类，则利用get方法设置值
            int index = 1; // PreparedStatement 参数索引从1开始
            for (String key : seqArray) {
                // 构造getter方法名称
                String methodName = "get" + capitalize(key);
                // 获取方法对象
                Method method = paramObject.getClass().getMethod(methodName);
                // 调用方法并获取返回值
                Object value = method.invoke(paramObject);
                // 设置到PreparedStatement中
                pst.setObject(index++, value);
            }
        }
        //获取影响行数
        return pst.executeUpdate();
    }

    /**
     * 更新语句
     * @param sqlName
     * @param paramObject
     * @return
     * @throws SQLException
     */
    public int Update(String sqlName,Object paramObject) throws Exception {
       String Sql=findSql("update", sqlName);
        if (Sql == null) {
            throw new Exception("mapper中不存在调用放法");
        }
        //预编译sql语句
        pst = conn.prepareStatement(Sql);
        //sql语句参数设置(用户传pojo对象 or 用户传map有序无需对象)
        //用户传LinkedHashMap有序对象，来保持插入顺序
        if (paramObject instanceof Map){//判断是Map
            int i = 0;// 假设这是你要开始设置参数的位置索引-1
            // 按照插入顺序取出所有的值并设置到 PreparedStatement 中
            for(int j=0;j<seqArray.size();j++){
                Object value = ((Map<?, ?>) paramObject).get(seqArray.get(j));
                pst.setObject(++i, value);
            }
        }else {//如果是实体类，则利用get方法设置值
            int index = 1; // PreparedStatement 参数索引从1开始
            for (String key : seqArray) {
                // 构造getter方法名称
                String methodName = "get" + capitalize(key);
                // 获取方法对象
                Method method = paramObject.getClass().getMethod(methodName);
                // 调用方法并获取返回值
                Object value = method.invoke(paramObject);
                // 设置到PreparedStatement中
                pst.setObject(index++, value);
            }
        }
        //获取影响行数
        return pst.executeUpdate();
    }


    /**
     * @param sqlName
     * @param paramObject
     * @return
     * @throws SQLException
     */
    public int Delete(String sqlName,Object paramObject) throws Exception {
       String Sql=findSql("delete", sqlName);
        if (Sql == null) {
            throw new Exception("mapper中不存在调用放法");
        }
        pst = conn.prepareStatement(Sql);
        pst.setObject(1, paramObject);//这里只用实现最简单的根据id删除
        return pst.executeUpdate();
    }

    /**
     * 查询一个语句（只实现根据id查询一个）
     * @param sqlName
     * @param paramObject
     * @return
     * @throws SQLException
     */
    public  <T> List<T> SelectOne(String sqlName,Object paramObject) throws Exception {
        String Sql=findSql("selectOne", sqlName);
        Class clazz = Class.forName(resultType);//通过类型字符串，利用java反射技术获取到pojo类
        //预编译
        pst = conn.prepareStatement(Sql);
        pst.setObject(1, paramObject);//放入id
        resultSet = pst.executeQuery();//执行查询语句

        List list=new ArrayList<>();//准备返回List
        while (resultSet.next()) {//遍历处理每一条查询记录
            T entity = (T) clazz.getDeclaredConstructor().newInstance(); // 通过反射使用无参构造函数创建实例T
            for (Field field : clazz.getDeclaredFields()) {//通过反射，遍历类的属性
                field.setAccessible(true); // 允许访问私有字段
                String columnName = field.getName();//获取类的属性名称“carName”
                Object value = resultSet.getObject(columnName); // 利用类的属性名称“carName”去获取结果集的列值，假设数据库列名与实体字段名相同或者已经通过AS重命名
                field.set(entity, value); // 将值设置给实体字段
            }
            list.add(entity);//添加实体
        }
        return list;
    }


    /**
     * 查询列表语句（只实现查询全部，无条件）
     * @param sqlName
     * @return
     * @param <T>
     */
    public <T> List<T> SelectList(String sqlName) throws Exception {
        //准备SQL
        String Sql=findSql("selectMany", sqlName);
        if (resultType == null||resultType.equals("")) {
            throw new Exception("mapper的结果集类型未写");
        }
        Class<T> clazz = (Class<T>) Class.forName(resultType);
        //预编译
        pst = conn.prepareStatement(Sql);
        resultSet = pst.executeQuery();
        List list=new ArrayList<>();
        while (resultSet.next()) {
            T entity = clazz.getDeclaredConstructor().newInstance(); // 使用无参构造函数创建实例
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // 允许访问私有字段
                String columnName = field.getName();
                Object value = resultSet.getObject(columnName); // 假设数据库列名与实体字段名相同
                field.set(entity, value); // 将值设置给实体字段
            }
            list.add(entity);
        }
        return list;
    }

    /**
     * 事务的提交模式
     */
    public void commitMode(boolean value) throws SQLException {
        if (value) {//true时候，关闭事务
            conn.setAutoCommit(false); // 自动提交模式
        } else {
            conn.setAutoCommit(false); // 手动提交模式
        }
    }
    public void commit() throws SQLException {
        conn.commit();
    }

    /**
     * 会话的关闭
     */
    public void close() throws SQLException {
        conn.close(); // 关闭连接
    }

    public String findSql(String sqlType,String sqlName) {
        //准备SQL
        AtomicReference<String> sqlValue=new AtomicReference<>();
        if(sqlName.contains(".")) {
            for(int i=0;i<sqlList.size();i++) {
                Map<String,Object> tempMap=sqlList.get(i);
                if(tempMap.get("nameSpaceAndFunction").equals(sqlName)){
                    sqlValue.set((String) tempMap.get("sqlValue"));
                    if(sqlType.equals("selectOne")||sqlType.equals("selectMany")){
                        this.resultType=tempMap.get("resultType").toString();
                    }
                    if (sqlType.equals("insert")||sqlType.equals("update")) {
                        seqArray = (List<String>) tempMap.get("seq");
                    }
                }
            }
        }else {
            for(int i=0;i<sqlList.size();i++) {
                Map<String,Object> tempMap=sqlList.get(i);
                if(tempMap.get("function").equals(sqlName)){
                    sqlValue.set((String) tempMap.get("sqlValue"));
                    if(sqlType.equals("selectOne")||sqlType.equals("selectMany")){
                        this.resultType=tempMap.get("resultType").toString();
                    }
                    if (sqlType.equals("insert")||sqlType.equals("update")) {
                        seqArray = (List<String>) tempMap.get("seq");
                    }
                }
            }
        }
        return sqlValue.get();
    }
}
