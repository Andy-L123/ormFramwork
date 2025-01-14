package com.codeUp;
import org.junit.Test;
import org.liu.batis.core.SqlSessionLiu;
import org.liu.batis.core.SqlSessionLiuFactory;
import org.liu.batis.core.SqlSessionLiuFactoryBuilder;
import org.liu.batis.pojo.Car;
import org.liu.batis.utils.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class test {
    @Test
    public void testInsert() throws Exception {
        SqlSessionLiuFactoryBuilder builder = new SqlSessionLiuFactoryBuilder();
        SqlSessionLiuFactory sqlSessionLiuFactory= builder.build(Resource.getResourceAsStream("mybatis-config.xml"));
        SqlSessionLiu sqlSessionliu = sqlSessionLiuFactory.openSession();
        // 手动创建 Map 并填充数据
        Map<String, Object> carMap = new LinkedHashMap<>();
        carMap.put("id", "1");
        carMap.put("carNum", "LR29322");
        carMap.put("brand", "手写框架");
        carMap.put("guidePrice", 150000.00);
        carMap.put("produceTime", "2023-11-15");
        carMap.put("carType", "燃油车");
        sqlSessionliu.Insert("insertCar",carMap);

        Car car = new Car();
        car.setId(2L);
        car.setCarNum("LR29322");
        car.setBrand("广汽传祺");
        car.setGuidePrice(BigDecimal.valueOf(150000.00));
        car.setProduceTime("2023-11-15");
        car.setCarType("燃油车");
        sqlSessionliu.Insert("insertCar",car);
//        sqlSessionliu.ExtractPlaceholders();
    }

    @Test
    public void testUpdate() throws Exception {
        SqlSessionLiuFactoryBuilder builder = new SqlSessionLiuFactoryBuilder();
        SqlSessionLiuFactory build = builder.build(Resource.getResourceAsStream("mybatis-config.xml"));
        SqlSessionLiu sqlSessionliu = build.openSession();
        Car car = new Car();
        car.setId(57L);
        car.setCarNum("LR29322");
        car.setBrand("广汽传祺xxxxx");
        car.setGuidePrice(BigDecimal.valueOf(150000.00));
        car.setProduceTime("2023-11-15");
        car.setCarType("燃油车");
        int count=sqlSessionliu.Update("updateCar",car);
        System.out.println(count);
    }
    @Test
    public void testDelete() throws Exception {
        SqlSessionLiuFactoryBuilder builder = new SqlSessionLiuFactoryBuilder();
        SqlSessionLiuFactory sqlSessionLiuFactory = builder.build(Resource.getResourceAsStream("mybatis-config.xml"));
        SqlSessionLiu sqlSessionliu = sqlSessionLiuFactory.openSession();
        sqlSessionliu.commitMode(false);
        int count=sqlSessionliu.Delete("delCar",57L);
        System.out.println(count);
        sqlSessionliu.commit();
        sqlSessionliu.close();
    }
    @Test
    public void selectOne() throws Exception {
        SqlSessionLiuFactoryBuilder builder = new SqlSessionLiuFactoryBuilder();
        SqlSessionLiuFactory build = builder.build(Resource.getResourceAsStream("mybatis-config.xml"));
        SqlSessionLiu sqlSessionliu = build.openSession();
        List<Car> list=sqlSessionliu.SelectOne("selectOne",58L);
        list.forEach(car -> System.out.println(car));
    }
    @Test
    public void selectMany() throws Exception {
        SqlSessionLiuFactoryBuilder builder = new SqlSessionLiuFactoryBuilder();
        SqlSessionLiuFactory build = builder.build(Resource.getResourceAsStream("mybatis-config.xml"));
        SqlSessionLiu sqlSessionliu = build.openSession();
        List<Car> list=sqlSessionliu.SelectList("selectMany");
        list.forEach(car -> System.out.println(car+"1"));
    }


}
