package cn.pukkasoft.datasync;


import cn.pukkasoft.datasync.advice.AreaCodeEnum;
import cn.pukkasoft.datasync.advice.PlatFormEnum;
import cn.pukkasoft.datasync.advice.TimeUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
public class DatasyncApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void printDate2Str(){
        try {
            System.out.println(TimeUtils.parseDate("2020-11-23"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void printEnum3(){
        System.out.println(PlatFormEnum.getAll());
    }
    @Test
    public void printGetEnumEQ() throws NoSuchFieldException {
        Field[] declaredFields = AreaCodeEnum.class.getDeclaredFields();
        Field code = AreaCodeEnum.class.getDeclaredField("code");
        System.out.println(code);

    }

    @Test
    public void printTime(){
        String str=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));

        System.out.println(str);
    }

    @Test
    public void printKongStr() throws ParseException {
        System.out.println(TimeUtils.parseDate(TimeUtils.parseDate(""))+"");
    }

}
