package com.stars.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {

	private static final boolean EnableDebugMessage = true;
	private static final boolean Debug = false;
	
	public static double getJulianDate(int yr, int mo, int day, double hr) {
		double julian = 0.0;

		julian = 367.0 * (double) yr;
		julian -= (double) (int) (7.0 * ((double) yr + (double) (int) (((double) mo + 9.0) / 12.0)) / 4.0);
		julian -= (double) (int) (3.0 * ((double) (int) (((double) yr + ((double) mo - 9.0) / 7.0) / 100.0 + 1.0)) / 4.0);
		julian += (double) (int) (275.0 * (double) mo / 9.0) + (double) day
				+ 1721028.5;

		julian = julian + hr / 24.0;

		return julian;
	}

	public static double getJulianDate0(int yr, int mo, int day) {
		double julian = 0.0;

		julian = 367.0 * (double) yr;
		julian -= (double) (int) (7.0 * ((double) yr + (double) (int) (((double) mo + 9.0) / 12.0)) / 4.0);
		julian -= (double) (int) (3.0 * ((double) (int) (((double) yr + ((double) mo - 9.0) / 7.0) / 100.0 + 1.0)) / 4.0);
		julian += (double) (int) (275.0 * (double) mo / 9.0) + (double) day
				+ 1721028.5;
		
		return julian;
	}
	
	public static double getGMST0(int yr, int mo, int day, UnitType ut) {

		double julian = Util.getJulianDate0(yr, mo, day);
		double d0 = julian - 2451545.0;

		double gmst = 6.697374558 + 0.06570982441908 * d0;//+ 1.00273790935 * hr;

		/*
		 * 
		 * add correction code here
		 * 
		 * reference: http://aa.usno.navy.mil/faq/docs/GAST.php
		 * http://tycho.usno.navy.mil/sidereal.html
		 * http://www.csgnetwork.com/siderealjuliantimecalc.html
		 */

		while (gmst < 0.0)
			gmst += 24.0;

		gmst = gmst % 24.0; // hour based

		switch (ut) {
		case DEGREE:
			return Util.ConvertUnit(gmst, UnitConv.HourToDegree);
		case RAD:
			return Util.ConvertUnit(gmst, UnitConv.HourToRad);
		default:
			break;
		}

		return gmst;
	}

	public static double[] getGMST0Ext1(int yr, int mo, int day, UnitType ut) {
		
		double[] result = new double[2];
		
		double julian = Util.getJulianDate0(yr, mo, day);
		double d0 = julian - 2451545.0;

		double gmst = 6.697374558 + 0.06570982441908 * d0;//+ 1.00273790935 * hr;

		/*
		 * 
		 * add correction code here
		 * 
		 * reference: http://aa.usno.navy.mil/faq/docs/GAST.php
		 * http://tycho.usno.navy.mil/sidereal.html
		 * http://www.csgnetwork.com/siderealjuliantimecalc.html
		 */

		while (gmst < 0.0)
			gmst += 24.0;

		gmst = gmst % 24.0; // hour based

		switch (ut) {
		case DEGREE:
			gmst = Util.ConvertUnit(gmst, UnitConv.HourToDegree);
		case RAD:
			gmst = Util.ConvertUnit(gmst, UnitConv.HourToRad);
		default:
			break;
		}

		result[0] = gmst;
		result[1] = julian;
		
		return result;
	}

	
	/**
	 * @param yr
	 * @param mo
	 * @param day
	 * @param hr
	 * @param longitude
	 *            : hour based
	 */
	public static double getLMST(int yr, int mo, int day, double hr,
			double longitude, UnitType ut) {
		double gmst = getGMST0(yr, mo, day, UnitType.HOUR);

		double lst = gmst + longitude + 1.00273790935 * hr;
		
		while (lst < 0.0) {
			lst += 24.0;
		}

		lst %= 24.0;

		switch (ut) {
		case DEGREE:
			return Util.ConvertUnit(lst, UnitConv.HourToDegree);
		case RAD:
			return Util.ConvertUnit(lst, UnitConv.HourToRad);
		default:
			break;
		}

		return lst;

	}

	/**
	 * @param lst
	 *            : hour based
	 * @param ra
	 *            : hour based
	 * @return
	 */
	public static double getHA(double lst, double ra, UnitType ut) {
		double ha = lst - ra;

		while (ha < 0.0) {
			ha += 24.0;
		}

		ha %= 24.0;

		switch (ut) {
		case DEGREE:
			return Util.ConvertUnit(ha, UnitConv.HourToDegree);
		case RAD:
			return Util.ConvertUnit(ha, UnitConv.HourToRad);
		default:
			break;
		}

		return ha;

	}

	public static double getALT(double dec, double lat, double ha, UnitType ut) // hour
																			// based
	{
		double alt = 0.0;
		double decRad = ConvertUnit(dec,UnitConv.HourToRad);
		double latRad = ConvertUnit(lat,UnitConv.HourToRad);
		double haRad  = ConvertUnit(ha,UnitConv.HourToRad);

		alt = Math.asin(Math.sin(decRad)
				* Math.sin(latRad) + Math.cos(latRad)
				* Math.cos(haRad)*Math.cos(decRad));

		while (alt < 0.0) {
			alt += Math.PI*2 ;
		}

		alt %=Math.PI*2;
		
		if(alt > Math.PI){
			alt -= Math.PI*2.0;			
		}
			
		switch (ut) {
		case DEGREE:
			return Util.ConvertUnit(alt,UnitConv.RadToDegree);
		case HOUR:
			return Util.ConvertUnit(alt,UnitConv.RadToHour);
		default:
			break;
		}

		return alt;

	}

	public static double getAZ(double dec, double lat, double alt,double ha, UnitType ut) // hour
																			// based
	{
		double az = 0.0;
		double decRad = ConvertUnit(dec,UnitConv.HourToRad);
		double latRad = ConvertUnit(lat,UnitConv.HourToRad);
		double altRad = ConvertUnit(alt,UnitConv.HourToRad);
		double haRad  = ConvertUnit(ha,UnitConv.HourToRad);

		az = Math.acos((Math.sin(decRad) - Math
				.sin(altRad) * Math.sin(latRad))
				/ (Math.cos(altRad) * Math.cos(latRad)));

		while (az < 0.0) {
			az += Math.PI*2;
		}

		az %= Math.PI*2;

		if(Math.sin(haRad) < 0.0)
		{
			
		}
		else
		{
			az = Math.PI*2 - az;
		}
		
		switch (ut) {
		case DEGREE:
			return Util.ConvertUnit(az, UnitConv.RadToDegree);
		case HOUR:
			return Util.ConvertUnit(az, UnitConv.RadToHour);
		default:
			break;
		}

		return az;
	}

	public static Calendar[] StarRiseSet(double ra,double dec,double lat, double longi, int year, int mon, int day){
	
		boolean dayshift1 = false;
		boolean dayshift2 = false;
		Calendar[] calendar = new Calendar[2];
		
		double decRad = dec*Math.PI/180.0;
		double latRad = lat*Math.PI/180.0;
		
		double den = Math.cos(decRad)*Math.cos(latRad);
		double num = Math.sin(decRad)*Math.sin(latRad);
		double sv = Math.sin(34.0/60.0*Math.PI/180.0); 
		double cv = Math.cos(34.0/60.0*Math.PI/180.0);
						
		double H =  Math.acos(-((sv+num)/den)); // between -1 and 1
											   // out of range is impossible or should be respond with error
		
		H = Util.ConvertUnit(H, UnitConv.RadToHour);
		
		if(EnableDebugMessage)
			System.out.println("H: " + H);
		
		double LSTr = Util.ConvertUnit(ra,UnitConv.DegreeToHour) - H;
		
		if(LSTr > 24.0)
		{
			dayshift1 = true;
			LSTr = LSTr - 24.0;
		}	
		
		double LSTs = Util.ConvertUnit(ra,UnitConv.DegreeToHour) + H;
		
		if(LSTs > 24.0)
		{
			dayshift2 = true;
			LSTs = LSTs - 24.0;
		}
		
		if(EnableDebugMessage)
			System.out.println("LST: " + LSTr + " " + LSTs);
		
		double Ar = Math.acos( (Math.sin(decRad)+sv*Math.sin(latRad))/(cv*Math.cos(latRad)));
		double As = 2.0*Math.PI - Ar;
		
		if(EnableDebugMessage)
			System.out.println("Ar: " + Ar*180.0/Math.PI + ", As: " + As*180.0/Math.PI );
		
		double GSTr = ConvertLSTtoGST(LSTr,Util.ConvertUnit(longi, UnitConv.DegreeToHour));
		double GSTs = ConvertLSTtoGST(LSTs,Util.ConvertUnit(longi, UnitConv.DegreeToHour));
		
		if(GSTr < 0)GSTr += 24.0;
		GSTr %= 24.0;
		
		if(GSTs < 0)GSTs += 24.0;
		GSTs %= 24.0;
		
		
		double[] gmstJul = Util.getGMST0Ext1(year,mon,day, UnitType.HOUR);
		
		
		double UTr = Util.ConvertGSTtoUT(GSTr,gmstJul[0]); 
		double UTs = Util.ConvertGSTtoUT(GSTs,gmstJul[0]);
		
		if(EnableDebugMessage)
			System.out.println("UT: " + UTr + " " + UTs);
		
		calendar[0] = Util.ConvertUTtoLocal(UTr, gmstJul[1]);
		calendar[1] = Util.ConvertUTtoLocal(UTs, gmstJul[1]);
		
		if(dayshift1)
			calendar[0].add(Calendar.DATE, 1);

		
		if(dayshift2)
			calendar[1].add(Calendar.DATE, 1);
		
		return calendar;
	}
	
	public static Calendar[] SunRiseSet(int yr, int mo, int day, double lw,double ln){
		
		double js;
		double ep = 0.0;
		double C = 0.0;
		double M = 0.0;
		double jtransit = 0.0;
		double ns = 0.0;
		double n = 0.0;
		
		Calendar[] cal = new Calendar[4];
		double[] result = new double[2];
		
		
		double jdate = getJulianDate(yr,mo,day,12.0);

		ns = (jdate - 2451545.0009) - (lw/360.0);
		n = Math.round(ns);

		/* approximate solar noon */
		js = 2451545.0009 + (lw/360.0) + n;

		/*Mean solar anomaly , rad */
		M =   ((357.5291 + 0.98560028 * (js - 2451545.0)) % 360.0) * Math.PI / 180.0;

		/* equation of center*/
		C = 1.9148 * Math.sin(M) + 0.02 * Math.sin(2.0 * M) + 0.0003 * Math.sin(3.0 * M); 
			
		/* ecliptical longitude of the Sun */
		ep =  (M*180.0/Math.PI + 102.9372 + C + 180.0) % 360.0 * Math.PI/180.0;;

		jtransit = js + (0.0053 * Math.sin(M)) - (0.0069 * Math.sin(2.0 * ep));
		
		System.out.println( M + " " + C + " " +ep + " " + jtransit);

		
		/* declination of the Sun */
		double ds = (double) Math.asin( Math.sin(ep) * Math.sin(23.45*Math.PI/180.0));
		/*Hour Angle*/
		double ha =  (double) Math.acos( (Math.sin(-0.83*Math.PI/180.0) - Math.sin(ln*Math.PI/180.0) * Math.sin(ds))/(Math.cos(ln*Math.PI/180.0) * Math.cos(ds)));
		ha = ha*180.0/Math.PI;
		
		double hap =  (double) Math.acos( (Math.cos(108.0*Math.PI/180.0) - Math.sin(ln*Math.PI/180.0) * Math.sin(ds))/(Math.cos(ln*Math.PI/180.0) * Math.cos(ds)));
		
		double jss =  2451545.0009 + ((ha + lw)/360.0) + (double)n;
		
		result[1] = jss + 0.0053 * Math.sin(M) - 0.0069 * Math.sin(2.0 * ep);
		result[0] = jtransit-(result[1]-jtransit);
		
		if(true){//EnableDebugMessage){
			System.out.println("jdate: " + jdate);
			System.out.println("ns: " + ns);
			System.out.println("n: " + n);
			System.out.println("js: " + js);
			System.out.println("M: " + M + " " + M*180.0/Math.PI);
			System.out.println("C: " + C);
			System.out.println("ep: " + ep + " " + ep*180.0/Math.PI);
			System.out.println("jtransit: " + jtransit);
			System.out.println("ds: " + ds + " " + ds*180.0/Math.PI);
			System.out.println("ha: " + ha);
			System.out.println("jss: " + jss);
		}
		
		double t = (hap-ha)/(15.0*1.00273790935*24.0);
		System.out.println("time = " + t);
		
		cal[0] = Util.ConvertJuliantoUTC(result[0]);
		cal[1] = Util.ConvertJuliantoUTC(result[1]);
		cal[2] = Util.ConvertJuliantoUTC(result[0]+t);
		cal[3] = Util.ConvertJuliantoUTC(result[1]-t);		
		return cal;
	}
	
	public static Calendar[] SunRiseSetWithElevation(int yr, int mo, int day, double lw,double ln,double elevation/*feet*/){
		
		double js;
		double ep = 0.0;
		double C = 0.0;
		double M = 0.0;
		double jtransit = 0.0;
		double ns = 0.0;
		double n = 0.0;
		
		Calendar[] cal = new Calendar[4];
		double[] result = new double[2];
		
		double jdate = getJulianDate(yr,mo,day,12.0);

		ns = (jdate - 2451545.0009) - (lw/360.0);
		n = Math.round(ns);

		/* approximate solar noon */
		js = 2451545.0009 + (lw/360.0) + n;

		/*Mean solar anomaly , rad */
		M =   ((357.5291 + 0.98560028 * (js - 2451545.0)) % 360.0) * Math.PI / 180.0;

		/* equation of center*/
		C = 1.9148 * Math.sin(M) + 0.02 * Math.sin(2.0 * M) + 0.0003 * Math.sin(3.0 * M); 
			
		/* ecliptical longitude of the Sun */
		ep =  (M*180.0/Math.PI + 102.9372 + C + 180.0) % 360.0 * Math.PI/180.0;;

		jtransit = js + (0.0053 * Math.sin(M)) - (0.0069 * Math.sin(2.0 * ep));
		
		System.out.println( M + " " + C + " " +ep + " " + jtransit);

		
		/* declination of the Sun */
		double ds = (double) Math.asin( Math.sin(ep) * Math.sin(23.45*Math.PI/180.0));
		/*Hour Angle*/
		double ha =  (double) Math.acos( (Math.sin((-0.83-1.15*Math.sqrt(elevation)/60.0)*Math.PI/180.0) - Math.sin(ln*Math.PI/180.0) * Math.sin(ds))/(Math.cos(ln*Math.PI/180.0) * Math.cos(ds)));
		ha = ha*180.0/Math.PI;
		
		double hap =  (double) Math.acos( (Math.cos(108.0*Math.PI/180.0) - Math.sin(ln*Math.PI/180.0) * Math.sin(ds))/(Math.cos(ln*Math.PI/180.0) * Math.cos(ds)));
		hap = hap*180.0/Math.PI;
		
		double jss =  2451545.0009 + ((ha + lw)/360.0) + (double)n;
		
		result[1] = jss + 0.0053 * Math.sin(M) - 0.0069 * Math.sin(2.0 * ep);
		result[0] = jtransit-(result[1]-jtransit);
		
		if(EnableDebugMessage){
			System.out.println("jdate: " + jdate);
			System.out.println("ns: " + ns);
			System.out.println("n: " + n);
			System.out.println("js: " + js);
			System.out.println("M: " + M + " " + M*180.0/Math.PI);
			System.out.println("C: " + C);
			System.out.println("ep: " + ep + " " + ep*180.0/Math.PI);
			System.out.println("jtransit: " + jtransit);
			System.out.println("ds: " + ds + " " + ds*180.0/Math.PI);
			System.out.println("ha: " + ha);
			System.out.println("jss: " + jss);
		}
		
		double t = (hap-ha)/(15.0*1.00273790935*24.0);
		System.out.println("time = " + t);
		
					
		cal[0] = Util.ConvertJuliantoUTC(result[0]);
		cal[1] = Util.ConvertJuliantoUTC(result[1]);
		
		cal[2] = Util.ConvertJuliantoUTC(result[0]+t);
		cal[3] = Util.ConvertJuliantoUTC(result[1]-t);

		
		
		return cal;
	}
	
	public static Calendar ConvertJuliantoUTC(double jd){
		
		 StringBuilder sb = new StringBuilder();
		
		 double z = Math.floor(jd+0.5);
		 double w = Math.floor((z - 1867216.25)/36524.25);
		 double x = Math.floor(w/4);
		 double a = z+1+w-x;
		 double b = a+1524;
		 double c = Math.floor((b-122.1)/365.25);
		 double d = Math.floor(365.25*c);
		 double e = Math.floor((b-d)/30.6001);
		 double f = Math.floor(30.6001*e);
		 
		 int day = (int)(b-d-f);
		 
		 int month = 0;
		 
		 if((long)e > 13){
			 month = (int)(e-13);
		 }
		 else {
			 month = (int)(e-1);
		 }
		 
		int year = 0;
		
		if((month == 1) || (month == 2))
		{
			year = (int)(c-4715);
		}
		else
		{
			year = (int)(c-4716);
		}
		
		 double hh = Util.getFraction(jd+0.5)*24.0;
		 double mm = Util.getFraction(hh)*60.0;
		 double ss = Util.getFraction(mm)*60.0;
		 
		 if(EnableDebugMessage && Debug){
			 System.out.println("day: " + day + ", month: " + month + ", year: " + year);
			 System.out.println((long)hh+ ":" + (long)mm + ":" + (long)ss);
		 }
		 
		 sb.append(year);
		 sb.append("-");
		 sb.append(month);
		 sb.append("-");
		 sb.append(day);
		 sb.append(" ");
		 sb.append((long)hh);
		 sb.append(":");
		 sb.append((long)mm);
		 sb.append(":");
		 sb.append((long)ss);
		 
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 df.setTimeZone(TimeZone.getTimeZone("GMT"));
		 Calendar cal = Calendar.getInstance();
		 
		 Date date = null;
		try {
			date = df.parse(sb.toString());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		cal.setTime(date);
		
		 return cal;
	}
	
	public static double ConvertUnit(double value, UnitConv trans) {
		switch (trans) {
		case DegreeToRad:
			return value * (double) Math.PI / 180.0;
		case DegreeToHour:
			return value / 15.0;
		case HourToRad:
			return value * (double) Math.PI / 12.0;
		case HourToDegree:
			return value * 15.0;
		case RadToDegree:
			return value * 180.0 / (double) Math.PI;
		case RadToHour:
			return value * 12.0 / (double) Math.PI;
		case TimeToHour:
			return value;
		case NoConv:
			return value;
		default:
			break;
		}
		throw new UnsupportedOperationException(
				"Invalid operation for unit conversion");
	}

	public static double convertString(String prev,UnitConv ut) {
		double ret = 0.0;
		String[] item = null;

		try {
			item = prev.split(":");

			if (item.length < 3) {

				ret = Double.parseDouble(item[0]) + Double.parseDouble(item[1])
						/ 60.0;

			} else {
				ret = Double.parseDouble(item[0]) + Double.parseDouble(item[1])
						/ 60.0 + Double.parseDouble(item[2]) / 3600.0;
			}

			if (item.length > 3) {
				char ch = item[3].charAt(0);

				if (ch == 'S') {
					ret = ret * -1.0;
				} else if (ch == 'W') {
					ret = ret * -1.0;
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}

		return ConvertUnit(ret, ut);
	}

	public static double DaysFromJ2000(Calendar cur) {

		long diff = 0;
		Calendar j2k = Calendar.getInstance();

		j2k.clear();

		j2k.set(2000, Calendar.JANUARY, 1, 11, 58, 55); // J2000 with UTC

		diff = cur.getTimeInMillis() - j2k.getTimeInMillis();

		return (diff / (1000 * 60 * 60 * 24));

	}

	public static double getCurrentTime() {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		return Util.convertString(df.format(cal.getTime()).toString(),UnitConv.TimeToHour);

	}
	
	public static double getFraction(double number){
		long num = (long)number;
		return (double)(number-num);
	}

	public static double ConvertLSTtoGST(double lst,double longi){
		/* unit: hour */
		return lst - longi;
		
	}
	
	public static double ConvertGSTtoUT(double gst,double gmst0){
		/* unit: hour */
		double tmp = gst-gmst0;
		
		if(tmp <0.0) tmp += 24.0;
		if(tmp >24.0) tmp -= 24.0;
		
		return tmp/1.00273790935;
	}
	
	public static Calendar ConvertUTtoLocal(double ut,double julian){
		return Util.ConvertJuliantoUTC(ut/24.0 + julian);
	}
	
	
	
	
}
