package cn.pukkasoft.datasync.advice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mike on 2019/7/7.
 */
public class SafeUtil {

    public static Long getLong(Object obj,Long defvalue){
        Long tmpret = defvalue;
        if(obj!=null){
            try{
                tmpret = Long.parseLong(obj.toString());
            }catch(Exception ex){}
        }
        return tmpret;
    }
    public static int getInt(Object obj,int defvalue){
        int tmpret =defvalue;
        if(obj!=null){
            try{
                tmpret = Integer.parseInt(obj.toString());
            }catch(Exception ex){

            }
        }
        return tmpret;
    }
    public static Integer getInteger(Object obj){
        Integer tmpret = new Integer(0);
        if(obj!=null){
            try{
                tmpret=Integer.valueOf(obj.toString());
            }catch(Exception ex){

            }
        }
        return tmpret;
    }
    public static Integer getInteger(Object obj,int defvalue){
        Integer tmpret = new Integer(defvalue);
        if(obj!=null){
            try{
                tmpret= Integer.valueOf(obj.toString());
            }catch(Exception ex){

            }
        }
        return tmpret;
    }

    public static String getString(Object obj){
        String tmpret="";
        if(obj!=null)
            tmpret = obj.toString();
        return tmpret.trim();
    }

    public static String getString(Object obj,String defvalue){
        String tmpret=defvalue;
        if(obj!=null)
            tmpret = obj.toString();
        return tmpret.trim();
    }

    public static String getDateString(Date date, String dateFormat){
        String tmpret = "";
        try{
             if(date==null ||dateFormat==null)
                return "";

            DateFormat df = new SimpleDateFormat(dateFormat);
            tmpret = df.format(date);
           // System.out.println("==========>getDateString.date:"+date+",dateFormat:"+dateFormat+",tmpret="+tmpret);

        }catch (Exception e){

        }
        return tmpret;
    }

}
