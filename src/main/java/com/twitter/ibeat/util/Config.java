package com.twitter.ibeat.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.ServerAddress;

public final class Config {
	
	
	/****************************************************************
	 *  					CONSTANTS								*
	 ****************************************************************/
	 
	
	public static String HOST_SCRIPT   = "192.168.22.55";
	
	public static List<ServerAddress> HOST_HISTORICAL = new ArrayList<ServerAddress>() {
		private static final long serialVersionUID = 1L;
		{
			try {
				add(new ServerAddress("192.168.33.209", 27017));
				add(new ServerAddress("192.168.33.210", 27017));
				add(new ServerAddress("192.168.33.211", 27017));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	};
	
	public static String HOST_ARTICLE_READ   = "192.168.22.55";
	public static String HOST_ARTICLE_WRITE   = "192.168.22.55";
	public static String HOST_ARTICLE_SCRIPT   = "192.168.22.55";
	
	public static String DBNAME = "ibeat";
	public static String DBNAME_BUSINESS = "ibeatBusiness";
	public static String DBNAME_ARTICLE_DB = "ibeatArticleDB";
	public static String DBNAME_AUTHOR_DB = "ibeatAuthor";
	public static String DBNAME_WEEK_DB = "ibeatWeek";
	public static String DBNAME_MONTH_DB = "ibeatMonth";
	public static String DBNAME_HISTORY_DB = "ibeatHistory";
	public static String DBNAME_PROCESS_DB = "ibeatProcessDB";
	public static String DBNAME_ANALYTIC_DB = "analytics";
	public static String DBNAME_TWITTER_DB = "TIL_TWITTER_DB";

	public static String DBNAME_CACHE_DB = "ibeatCacheDB";
	public static String DBNAME_DASHBOARD_DB = "ibeatDashboard";
	
	//	Twitter Collection
	public static final String TWEET_COLLECTION = "tweetcollection";
	public static final String TWEET_ID_COLLECTION = "tweetIDcollection";
	
	public static final String TWITTER_LOG =  "twitterLog";
	public static final String HOURLY_TWEET_COLLECTION =  "hourlyTweetCount";
	public static final String DAILY_TWEET_COLLECTION =  "dailyTweetCollection";
	public static final String MONTHLY_TWEET_COLLECTION =  "monthlyTweetCollection";
	public static final String HANDLES_CENTER_COLLECTION =  "centerwisehandles";

	// Twitter Status Code
	public static final int OK = 200;
	public static final int Not_Modified = 304;
	public static final int Bad_Request = 400;
	public static final int Unauthorized = 401;
	public static final int Forbidden = 403;
	public static final int Not_Found = 404;
	public static final int Not_Acceptable = 406;
	public static final int Gone = 410;
	public static final int Enhance_Your_Calm = 420;
	public static final int Unprocessable_Entity = 422;
	public static final int Too_Many_Requests = 429;
	public static final int Internal_Server_Error = 500;
	public static final int Bad_Gateway = 502;
	public static final int Service_Unavailable = 503;
	public static final int Gateway_timeout = 504;
		
	
	// Cache Collections
	public static final String DAY_CACHE =  "DayCache";
	public static final String WEEK_CACHE =  "WeekCache";
		
	// Constants
	public static final int MINUTE	= 1;
	public static final int HOUR   	= 2;
	public static final int DAY   	= 3;
	public static final int WEEK   	= 4;
	public static final int MONTH   = 5;
	public static final int CUSTOM	= 6;
	public static final int MONTH_WEEK   = 7;
	public static final int FOREVER   = 8;
	public static final int YEAR   = 9;
	
	public static final long MINUTE_IN_MILLISEC = 300000l;
	public static final long HOUR_IN_MILLISEC = 300000*12l;
	public static final long DAY_IN_MILLISEC = 300000*12*24l;
	public static final long MONTH_IN_MILLISEC = 300000*12*24*30l;
	public static final long WEEK_IN_MILLISEC = 300000*12*24*7l;
	public static final Long YEAR_IN_MILLISEC = 300000*12*24*365l;
	
	public static final long MINIMUM_COUNT_FOR_POPULAR = 10;
	
	public static final String SEPRATOR_HOST = "@";
	public static final String SEPRATOR_CAT = ",";
	public static final String DEVICE_ID = "deviceID";
	
	// Multiple Make with for Same User
	public static final List<String> REGEX_VENDOR = Arrays.asList("mtech");

	public static String getStackTrace(Exception exception) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		exception.printStackTrace(printWriter);
		String s = writer.toString();
		return s;
	}
	
	public static boolean chkNull(String text) {
		//log.debug("Value : " + text);
		if(text!= null && !text.trim().equals("") && !text.equals("undefined")) {
			return false;
		}
		return true;
	}
}
