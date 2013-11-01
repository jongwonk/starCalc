package com.stars.util;

import java.util.Calendar;
import java.util.TimeZone;

public class UtilTest {

	public static void main(String[] args){

	  int Year = 2013;
	  int Month = 10;
	  int Day   = 30;
	  double Time = 15.0;
	  
	  // the Moon
	  String ra = "18:36:56";
	  String dec = "38:47:01";
	  
	  String longitude = "121:53:24:W";
	  String latitude  = "37:18:00:N";

/*
	  ra = "23:39:20";
	  dec = "21:42:00";

	  longitude = "64:00:0:E";
	  latitude  = "30:0:0:N";
*/
	  
	  double gmst = Util.getGMST0(Year, Month, Day, UnitType.HOUR);
	  System.out.println("GMST: "+gmst);
	  
	  
	  double longi = Util.convertString(longitude, UnitConv.DegreeToHour);
	  double lmst = Util.getLMST(Year, Month, Day, Time, longi,UnitType.HOUR);
	  System.out.println("LMST: " + lmst);
		
	  double raHour = Util.convertString(ra,UnitConv.NoConv);
	  double ha = Util.getHA(lmst, raHour, UnitType.HOUR);
	  System.out.println("HA: " + ha + " " + Util.ConvertUnit(ha, UnitConv.HourToDegree));
	  
	  double lati = Util.convertString(latitude, UnitConv.DegreeToHour);
	  System.out.println("Latitude: " + lati);
	  
	  double dec1 = Util.convertString(dec, UnitConv.DegreeToHour);
	  
	  double alt = Util.getALT(dec1, lati, ha, UnitType.HOUR);	  
	  System.out.println("ALT: " + alt  + " " + Util.ConvertUnit(alt, UnitConv.HourToDegree));
	  
	  double az =  Util.getAZ(dec1, lati, alt, ha, UnitType.HOUR);
	  System.out.println("AZ: " + az + " " + Util.ConvertUnit(az, UnitConv.HourToDegree));
	  
	  Calendar[] calSun = Util.SunRiseSetWithElevation(2013, 10, 30, 121.9000/* degree */ , 37.3333/* degree */,100/*ft*/);

	  System.out.println( calSun[0].getTime() + "\t" + calSun[1].getTime());
	  System.out.println( calSun[2].getTime() + "\t" + calSun[3].getTime());
	  
	  double raDeg = Util.ConvertUnit(raHour,UnitConv.HourToDegree);
	  double decDeg = Util.ConvertUnit(dec1, UnitConv.HourToDegree);
	  double latiDeg = Util.ConvertUnit(lati, UnitConv.HourToDegree);
	  double longiDeg = Util.ConvertUnit(longi, UnitConv.HourToDegree);
	  
	  Calendar[] cal00 = Util.StarRiseSet(raDeg, decDeg, latiDeg, longiDeg,2013,10,31);
	  System.out.println(cal00[0].getTime() + "\n" + cal00[1].getTime() );
  
	}

	
}



