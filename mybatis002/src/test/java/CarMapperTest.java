import com.codeup.mybatis.test.pojo.Car;
import com.codeup.mybatis.test.utils.SqlsessionUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CarMapperTest {
    @Test
    public void testInsert() {
        Map<String,String> map = new HashMap<String,String>();
        map.put("carNum", "LR29322");
        map.put("brand", "广汽传祺");
        map.put("price", "150000.00");
        map.put("time", "2023-11-15");
        map.put("type", "燃油车");
        SqlSession sqlSession = SqlsessionUtils.openSession();
        sqlSession.insert("insertCar", map);
        sqlSession.close();
    }
    @Test
    public void testInsertPojo() {
        Car car = new Car();
        car.setCarNum("LR29322");
        car.setBrand("广汽传祺");
        car.setGuidePrice(150000.00);
        car.setProduceTime("2023-11-15");
        car.setCarType("燃油车");
        SqlSession sqlSession = SqlsessionUtils.openSession();
        sqlSession.insert("insertCar", car);
        sqlSession.close();
    }
}
