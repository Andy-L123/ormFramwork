package org.liu.batis.utils;


import java.io.InputStream;

/**
 * liubatis框架提供的工具类
 * 完成类路径加载资源
 *
 * @author liu
 * @version 1.0
 * @since 1.0
 */
public class Resource {
    /**
     * 工具类的构造方法都是建议私有化的
     * 因为工具类方法都是静态的，不需要创建对象就能调用
     * 避免New对象，所有构造方法私有化
     * 此为编程习惯
     */
    private Resource() {

    }

    /**
     * 类路径加载资源封装
     * @param resource 类路径
     * @return 指向资源文件的输入流
     */
    public static InputStream getResourceAsStream(String resource) {
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }

}
