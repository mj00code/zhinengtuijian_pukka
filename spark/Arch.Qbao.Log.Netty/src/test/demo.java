package test;

import com.qbao.search.conf.Config;
import util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class demo {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) throws ParseException {
		List<String> dateListExist = new ArrayList<String>();
		dateListExist.add("adfasd");
		dateListExist.add("adfsdfasasd");
		System.out.println(dateListExist.toString().replace("[", "").replace("]", ""));
		String time =Config.get().get("end.day.test");
		
		
		String YYYY_MM_DD = sdf.format(DateUtil.dsDay_Date(new Date(),-1)) ;
		//YYYY_MM_DD = sdf.format(new Date()) ;
		System.out.println(YYYY_MM_DD);
	}
}
