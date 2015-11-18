package com.twitter.ibeat.iBeatTwitter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.til.ibeat.dao.sort.helper.GenericComparableHelper;
import com.til.ibeat.util.CommonUtils;
import com.til.ibeat.util.MailThread;
import com.twitter.ibeat.util.Config;

public class SocialMailer {
	private static final String SGMAILID = "sgajwani@gmail.com";
	private final String borderLeftStyle = " style=\"text-align: center;padding-left:5px;border-width:1px;border-left-style:solid;border-bottom-style:solid;";
	private final String borderRightStyle = "border-right-style:solid\"";

	private static Logger log = Logger.getLogger(SocialMailer.class);

	private final MongoClient mongoHistory;

	public SocialMailer() {
		mongoHistory = new MongoClient(Config.HOST_HISTORICAL,
				MongoClientOptions.builder().connectionsPerHost(10).threadsAllowedToBlockForConnectionMultiplier(15)
						.connectTimeout(5000).writeConcern(WriteConcern.NORMAL).build());
	}

	@Deprecated
	private Map<String,Object> getTwitterReport(Long startDate, Long endDate) {
		DBObject match = null;
		DBObject group = null;
		DBObject fields = new BasicDBObject("twHandle", 1);
		fields.put("_id", 0);
		fields.put("tweetID", 1);
		fields.put("count", 1);
		fields.put("rtwCount", 1);
		DBObject project = new BasicDBObject("$project", fields);
		// DBObject sortCount = new BasicDBObject("$sort", new
		// BasicDBObject("count", -1));
		Map<String, Object> dbObjIdMap = new HashMap<String, Object>();
		DBObject groupFields = null;
		AggregationOptions aggregationOptions = AggregationOptions.builder().batchSize(10000)
				.outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(true).build();
		Cursor results = null;

		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection coll = businessDB.getCollection(Config.TWEET_COLLECTION);

		match = new BasicDBObject("$match", new BasicDBObject("twDate",
				BasicDBObjectBuilder.start("$gt", new Date(startDate)).add("$lte", new Date(endDate)).get()));
		// /match = new BasicDBObject("$match", new
		// BasicDBObject("User","virensgi"));
		log.debug(match);
		dbObjIdMap.put("twHandle", "$twHandle");
		dbObjIdMap.put("tweetID", "$tweetID");

		groupFields = new BasicDBObject("_id", new BasicDBObject(dbObjIdMap));
		// groupFields.put("count", new BasicDBObject("$sum", 1));
		groupFields.put("rtwCount", new BasicDBObject("$last", "$rtwCount"));
		group = new BasicDBObject("$group", groupFields);
		results = coll
				.aggregate(Arrays.asList(match, project, group/* , sortCount */), aggregationOptions);
		Map<String,Object> usersMap = new HashMap<String, Object>();
		if (results != null) {
			while (results.hasNext()) {
				DBObject dbo = results.next();
				if (dbo != null) {
					DBObject id = (DBObject) dbo.get("_id");
					String user = (String) (id).get("twHandle");
					// Long tweetId = (Long) (id).get("tweetID");
					// String url = (String) dbo.get("url");
					Long rtwCount = (Long) dbo.get("rtwCount");
					// Integer twCount = (Integer) dbo.get("count");
					Map<String, Long> userMap = null;
					if (usersMap.containsKey(user)) {
						userMap = (Map<String, Long>) usersMap.get(user);
					} else {
						userMap = new HashMap<String, Long>();
					}
					Long tweetCount = 1l;
					/*
					 * try { tweetCount = new Long(twCount); } catch (Exception
					 * e) { e.printStackTrace(); } if (twCount == null) {
					 * tweetCount = 0l; }
					 */
					if (!userMap.isEmpty() && userMap.containsKey("tweetCount")) {
						tweetCount = userMap.get("tweetCount") + 1;
					}
					userMap.put("tweetCount", tweetCount);
					Long retweetCount = rtwCount;
					if (retweetCount == null) {
						retweetCount = 0l;
					}
					if (!userMap.isEmpty() && userMap.containsKey("retweetCount")) {
						retweetCount = retweetCount + userMap.get("retweetCount");
					}
					userMap.put("retweetCount", retweetCount);
					usersMap.put(user, userMap);
				}

			}
		}
		return usersMap;

	}

	public static void main(String[] args) {
		try {
			SocialMailer socialMailer = new SocialMailer();
			// socialMailer.getTwitterReport(1443897000000l, 1443983400000l);

//			args = new String[2];
//			args[0] = "E://mail.properties";
//			args[1] = "Delhi";

			if (args.length < 2) {
				log.error("Usage: SocialMailer [mailRecipientFile(1)]");
				System.exit(0);
			}

			log.info("_______________________________________Social Mailer Execution Time - " + new Date());

			String mailRecipientsFile = args[0];
			String location = args[1];

			Properties mainProperties = new Properties();
			FileInputStream file = null;
			try {
				file = new FileInputStream(mailRecipientsFile);
				mainProperties.load(file);
				file.close();
			} catch (FileNotFoundException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			}

			String tomailIds = mainProperties.getProperty("tomailIds");
			String ccmailIds = mainProperties.getProperty("ccmailIds");
			String bccmailIds = mainProperties.getProperty("bccmailIds");
			String frommailIds = mainProperties.getProperty("frommailIds");
			String fromName = mainProperties.getProperty("fromName");
			String subject = mainProperties.getProperty("subject");
			String hostName = mainProperties.getProperty("hosName");

			String title = mainProperties.getProperty("title");
			String tabTitle = mainProperties.getProperty("tabTile");
			String bannerColor = mainProperties.getProperty("bannerColor");
			String textColor = mainProperties.getProperty("textColor");
			String welcomeText = mainProperties.getProperty("welcomeText");

			if (title == null) {
				title = new String();
				log.error("Properties:Title null");
			}

			String html = socialMailer.createHTML(title, tabTitle, bannerColor, textColor, welcomeText,location);
			System.out.println(html);
			if (html != null && !html.isEmpty()) {

				if (tomailIds != null && !tomailIds.isEmpty()) {
					String[] recipients = tomailIds.split(";");
					for (int i = 0; i < recipients.length; i++) {
						if (recipients[i] != null && !recipients[i].isEmpty()) {
							String[] toArr = { recipients[i] };
							String[] ccRecipients = {};
							String[] bccRecipients = {};
							if (ccmailIds != null && !ccmailIds.isEmpty() && recipients[i].equalsIgnoreCase(SGMAILID)) {
								ccRecipients = ccmailIds.split(";");
							}
							if (bccmailIds != null && !bccmailIds.isEmpty()
									&& recipients[i].equalsIgnoreCase(SGMAILID)) {
								bccRecipients = bccmailIds.split(";");
							}
							if (hostName != null && !hostName.isEmpty()) {

							} else {
								hostName = "nmailer.indiatimes.com";
							}
							// hostName = "10.157.211.113";
							MailThread mailThread = new MailThread(toArr, frommailIds, fromName, ccRecipients, subject,
									html, hostName, bccRecipients, false);
							Thread th = new Thread(mailThread);
							th.start();
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(Config.getStackTrace(e));
		}

	}

	private String createHTML(String title, String tabTitle, String bannerColor, String textColor, String welcomeText, String location) {
		try {
			StringBuilder htmlCode = new StringBuilder();
			
			htmlCode.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			htmlCode.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			htmlCode.append("<head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			htmlCode.append("<title>Subscribe - "+tabTitle+" Newsletter</title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=yes\"><style type=\"text/css\">@media only screen and (max-width: 599px) {.outer_table { width: 100%;}}" 
					+ "@media only screen and (max-width: 400px) " +"{.bold_text {font-size: 24px !important; padding-left: 1% !important;}" +".logo {font-size: 1.5em !important;}" 
					+ "@media only screen and (min-width: 200px) and (max-width: 325px) " +"{.bold_text {font-size: 15px !important; padding-left: .5% !important;}" +".logo {font-size: 1em !important;}}" 
					+"}</style></head>");
			htmlCode.append("<body>");
			if (welcomeText != null) {
				SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MMMM/yyyy");

				welcomeText += "on <b>" + (formattedDate.format(DateUtils.addDays(new Date(), -1))).replace("/", " ")
						+ "</b> for <b>"+location+"</b>"+" location.";
				htmlCode.append(welcomeText).append("</p><br><br>");
			}
			htmlCode.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\" >");
			htmlCode.append("<td valign=\"top\">");
			htmlCode.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"  align=\"center\" class=\"outer_table\"><tr>");
			htmlCode.append(" <td colspan=\"3\" style=\"background-color:#"+bannerColor+"; height:70px; vertical-align: middle\" valign=\"top\" align=\"center\">");
			htmlCode.append("<a class=\"logo\" style=\"color: #"+textColor+";   display: inline-block;   font-family: times new roman;   font-size: 2.17em;   font-weight: bold;  text-decoration: none;   text-transform: uppercase; letter-spacing:0.5px; text-shadow:0px 0px 2px #fff; width: 100%;\" href=\"#\">"+title.toUpperCase()+"</a></td> </tr>");


			// Adding Data headers
			htmlCode.append(getTableHeader());
			htmlCode.append(getTwitterHandlesHTMLData());
			htmlCode.append("</table></td></table>");
			htmlCode.append("</div></div>");
			// Foot Note
			htmlCode.append("<br><br>Regards<br>Team");
			htmlCode.append(getDisclaimer());
			htmlCode.append("</body></html>");
			return htmlCode.toString();
		} catch (Exception e) {
			log.error(Config.getStackTrace(e));
		}
		return "";
	}

	private static String getTableHeader() {
		StringBuilder htmlCode = new StringBuilder();
		htmlCode.append(
				"<tr><td class=\"dataHeader\" style=\"text-align: center;width:40%;border-width:1px;border-left-style:solid;border-bottom-style:solid;padding: 5px;"
						+ "color: #C70626;border-color: #000000;\">Twitter Handles" + "</td>"
						+ "<td class=\"dataHeader\" style=\"text-align: center;width:33%;border-width:1px;border-left-style:solid;border-bottom-style:solid;padding: 5px;"
						+ "color: #C70626;border-color: #000000;\">Tweet Count" + "</td>"
						+ "<td class=\"dataHeader\" style=\"text-align: center;width:44% !important;border-width:1px;border-left-style:solid;border-bottom-style:solid;padding:5px;border-right-style:solid;"
						+ "color: #C70626;border-color: #000000;\">Re-Tweet Count" + "</td>" + "</tr>");
		return htmlCode.toString();
	}

	@SuppressWarnings("unchecked")
	private String getTwitterHandlesHTMLData() {
		StringBuilder htmlCode = new StringBuilder();

		long endTMillis = CommonUtils.getStartOfDay(new Date()).getTime();
		long startMillis = endTMillis - Config.DAY_IN_MILLISEC;
		log.info("From " + new Date(startMillis) + " to " + new Date(endTMillis));
//		Map<String, Object> usersMap = getTwitterReport(startMillis, endTMillis);
		Map<String, Object> usersMap = getTwitterReportFromAggregatedData(startMillis);
		System.out.println("Unsorted Map -"+usersMap);
		Map<String, Object> usersMapSorted = getSortedMapList(usersMap, Integer.MAX_VALUE, "tweetCount"); 
		System.out.println("sorted Map -"+usersMap);

		long tweetCount = 0, reTweetCount = 0;
		Map<String, Long> userData = new HashMap<String, Long>();
		if (usersMap != null) {
			for (String user : usersMapSorted.keySet()) {
				userData = (Map<String, Long>) usersMapSorted.get(user);
				tweetCount = getFomattedLongValue(userData.get("tweetCount"));
				reTweetCount = getFomattedLongValue(userData.get("retweetCount"));

				htmlCode.append("<tr><td class =\"browserData\"").append(borderLeftStyle).append("\">");
				htmlCode.append(WordUtils.capitalize(user)).append("</td>");
				htmlCode.append("<td class =\"browserData\"").append(borderLeftStyle).append("\">");
				htmlCode.append(getFormattedNumber(tweetCount)).append("</td>");
				htmlCode.append("<td class =\"browserData\"").append(borderLeftStyle).append(borderRightStyle)
						.append(">");
				htmlCode.append(getFormattedNumber(reTweetCount)).append("</td>");
				htmlCode.append("</tr>");
			}
		}
		return htmlCode.toString();
	}

	private Map<String, Object> getTwitterReportFromAggregatedData(long startMillis) {
		Map<String,Object> usersMap = new HashMap<String, Object>();
		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection collTweetDaily = businessDB.getCollection(Config.DAILY_TWEET_COLLECTION);
		DBObject searchObject = new BasicDBObject("ts",startMillis/1000);
		Cursor dbCursor = collTweetDaily.find(searchObject);
		if(dbCursor!=null){
			while (dbCursor.hasNext()){
				DBObject tFeed = dbCursor.next();
				String twHandle = (String) (tFeed).get("twHandle");
				// Long tweetId = (Long) (id).get("tweetID");
				// String url = (String) dbo.get("url");
				Long retweetCount = (Long) tFeed.get("rtwCount");
				Long tweetCount = (Long) tFeed.get("tweetCount");
				Map<String,Long> userMap = new HashMap<String, Long>();
				userMap.put("tweetCount", tweetCount);
				userMap.put("retweetCount", retweetCount);
				usersMap.put(twHandle, userMap);
			}
		}
		return usersMap;
	}

	private static long getFomattedLongValue(Long value) {
		long convertedValue = 0l;
		try {
			convertedValue = Long.parseLong(value + "");
		} catch (Exception e) {
			convertedValue = -1l;
		}
		return convertedValue;
	}

	private static String getDisclaimer() {
		StringBuilder htmlCode = new StringBuilder();
		htmlCode.append("<p style=\"font-size: 13px\"><br>Disclaimer:<br>");
		htmlCode.append("This Data is powered by Social samples.</p>");

		return htmlCode.toString();
	}
	
	 private static Map<String, Object> getSortedMapList(Map<String, Object> countMap, int count, String sortKey) {
	        try {
	            long t = System.currentTimeMillis();
	            List<GenericComparableHelper> ll = new LinkedList<GenericComparableHelper>();
	            GenericComparableHelper gac = null;
	            for (Map.Entry<String, Object> mp : countMap.entrySet()) {
	                @SuppressWarnings("unchecked")
	                Map<String, Object> mDetail = (Map<String, Object>) mp.getValue();
	                gac = new GenericComparableHelper(mp.getKey(), mDetail, sortKey);
	                ll.add(gac);
	            }
	            Collections.sort(ll);
	            Map<String, Object> countMapReturn = new LinkedHashMap<String, Object>();
	            int i = 0;
	            for (GenericComparableHelper tg : ll) {
	                if (i < count) {
	                    countMapReturn.put(tg.getKey(), tg.getmDetail());
	                }
	                i++;
	            }
	            log.error("Time taken to create Map  " + (System.currentTimeMillis() - t));
	            return countMapReturn;
	        } catch (Exception e) {
	            log.error(e);
	            System.out.println("error reading response:readAll - " + e);
	        }
	        return countMap;
	    }
	 
	    private static String getFormattedNumber(Object number) {
	        return NumberFormat.getNumberInstance(Locale.US).format(number);
	    }

}
