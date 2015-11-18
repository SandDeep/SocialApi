package com.twitter.ibeat.iBeatTwitter4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.twitter.ibeat.util.Config;
import com.twitter.ibeat.util.DateTimeUtils;

/**
 * Class to read twitter handles for different public profiles.
 * 
 * @author Virendra.Agarwal
 *
 */
public class TwitterTimeLine {
	private static Logger log = Logger.getLogger(TwitterTimeLine.class);
	private Twitter twitter = null;
	private final MongoClient mongoHistory;
	Map<String, Long> tweetIDMap = null;
	Map<String, UserInfo> handleMap = null;
	Properties handles = null;
	String[] twitterHandleNames = null;
	private AtomicInteger set = new AtomicInteger(1);

	
	/**
	 * Method to initiate twitter instance
	 * 
	 * @param twitterHandles
	 *            file path to get twitter handle names and Auth account details
	 * @throws MongoException
	 * @throws IOException
	 */
	public TwitterTimeLine(String twitterHandles,String handleSet) throws MongoException, IOException {
		mongoHistory = new MongoClient(Config.HOST_HISTORICAL,
				MongoClientOptions.builder().connectionsPerHost(10)
						.threadsAllowedToBlockForConnectionMultiplier(15)
						.connectTimeout(5000).writeConcern(WriteConcern.NORMAL)
						.build());
		handles = new Properties();
		FileInputStream file = new FileInputStream(twitterHandles);
		handles.load(file);
		file.close();
		String names = handles.getProperty("handles-"+handleSet);
		if (names != null && !names.isEmpty()) {
			twitterHandleNames = names.split(",");
		}
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(handles.getProperty("ConsumerKeySet"+set.get()));
		cb.setOAuthConsumerSecret(handles.getProperty("ConsumerSecretSet"+set.get()));
		cb.setOAuthAccessToken(handles.getProperty("AccessTokenSet"+set.get()));
		cb.setOAuthAccessTokenSecret(handles.getProperty("AccessTokenSecretSet"+set.get()));
		cb.setUseSSL(true);
		twitter = new TwitterFactory(cb.build()).getInstance();
	}

	public static void main(String[] args) {
		try {
			if (args.length < 2) {
				log.info("Insufficient arguments : Usage: TwitterTimeLine file path");
				System.exit(0);
			}
			String twitterHandles = args[0];
			String handleSet = args[1];
			TwitterTimeLine twitterTimeLine = new TwitterTimeLine(twitterHandles,handleSet);
			twitterTimeLine.populateCenterWiseHandles();
			//twitterTimeLine.getTweetsUsingPaging();
			
			//	twitterTimeLine.populateTweetMap();
			twitterTimeLine.getTweetsUsingQuery();
		} catch (Exception e) {
			log.error("Exception in TwitterTime API ---"
					+ Config.getStackTrace(e));
		}

	}

	/**
	 * Populate tweets collection on hourly basis
	 * 
	 * @param dbl
	 */
	private void populateTweetCollection(List<DBObject> dbl) {
		/*DB businessDB = null;
		try {
			businessDB=new MongoClient().getDB(Config.DBNAME_BUSINESS);
		} catch (UnknownHostException e) {
			log.error(Config.getStackTrace(e));
		}*/
		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection coll = businessDB.getCollection(Config.TWEET_COLLECTION);
		log.info("Collection status before update"+coll.count());
		coll.insert(dbl);
		log.info("Collection status after update"+coll.count());
	}

	/**
	 * Populate tweetid map
	 * @return
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Map<String, Long> populateTweetMap() {
		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection coll = businessDB.getCollection(Config.TWEET_ID_COLLECTION);
		tweetIDMap = new HashMap<String, Long>();
		DBCursor dbc = coll.find();
		
		while (dbc.hasNext()) {
			DBObject dbo = dbc.next();
			if (dbo != null) {
				String userName = (String) dbo.get("UserName");
				Long id;
				try {
					id = (Long) dbo.get("lastaccess");
				} catch (NumberFormatException e) {
					id = ((Double) (Double.parseDouble((dbo.get("lastaccess"))
							.toString()))).longValue();
				}
				tweetIDMap.put(userName, id);
			}
		}
		return tweetIDMap;
	}
	
	private Map<String, UserInfo> populateCenterWiseHandles() {
		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection coll = businessDB.getCollection(Config.HANDLES_CENTER_COLLECTION);
		handleMap = new HashMap<String, UserInfo>();
		DBCursor dbc = coll.find();
		
		while (dbc.hasNext()) {
			DBObject dbo = dbc.next();
			if (dbo != null) {
				String twitterHandle=(String) dbo.get("twHandle");
				String userName = (String) dbo.get("username");
				String center = "delhi";
				try {
					center = (String) dbo.get("center");
				} catch (Exception e) {
				}
				handleMap.put(twitterHandle, new UserInfo(center, userName));
			}
		}
		return handleMap;
	}

	/**
	 * UPdate tweetid with latest sinceid
	 * @param map
	 */
	@SuppressWarnings("unused")
	private void updateTweetIDMap(Map<String, Long> map) {
		DB businessDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DBCollection coll = businessDB
				.getCollection(Config.TWEET_ID_COLLECTION);
		for (String key : map.keySet()) {
			DBObject qObj = new BasicDBObject("UserName", key);
			DBObject updateObj = new BasicDBObject("UserName", key).append(
					"lastaccess", map.get(key));
			coll.update(qObj, updateObj, true, false);
		}
	}

	/**
	 * Tweet hits using query
	 * 
	 * @param twitter
	 * @param userNames
	 * @throws TwitterException
	 */
	private void getTweetsUsingQuery()
			throws TwitterException {
		
		for (int i = 0; i < twitterHandleNames.length; i++) {
			
			Query query = new Query("from:"+twitterHandleNames[i]+ " +exclude:retweets");
			query.setCount(100);
			int searchResultCount;
			long lowestTweetId = Long.MAX_VALUE;
			Date date = new Date(DateTimeUtils.getNDaysbackDailySliceStamp(1));
			String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
			System.out.println(modifiedDate);
			query.setSince(modifiedDate);
			date = new Date(DateTimeUtils.getDailySliceStamp());
			modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
			System.out.println(modifiedDate);
			query.setUntil(modifiedDate);
			List<DBObject> dbl = new ArrayList<DBObject>();
			int queryHits = 0;
			
			do {
				QueryResult queryResult = twitter.search(query);
				searchResultCount = queryResult.getTweets().size();
				
				for (Status st : queryResult.getTweets()) {
					if (!st.isRetweet() && (st.getUser().getScreenName().equalsIgnoreCase(twitterHandleNames[i]))) {
						URLEntity[] uEn = st.getURLEntities();
						StringBuilder url = new StringBuilder();
						for (URLEntity urle : uEn) {
							if (urle.getURL() != null && !urle.getURL().isEmpty()) {
								url.append(urle.getExpandedURL());
							}
						}
						dbl.add(createTweetObject(twitterHandleNames[i], st,
								url.toString(), handleMap.get(twitterHandleNames[i])));
					}
					if (st.getId() < lowestTweetId) {
			            lowestTweetId = st.getId();
			            query.setMaxId(lowestTweetId);
			        }
					}
				queryHits = queryHits+1;
				if(dbl.size() >= 200 || queryHits >=2){
					break;
				}
				

			} while (searchResultCount != 0 && searchResultCount % 100 == 0);
			
			if (dbl != null && !dbl.isEmpty()) {
				populateTweetCollection(dbl);
			}
		}
	}

	/**
	 * Tweet hits using paging
	 * 
	 * @throws TwitterException
	 */
	private void getTweetsUsingPaging() throws TwitterException{
		List<DBObject> dbl = new ArrayList<DBObject>();
		
		for (int i = 0; i < twitterHandleNames.length; i++) {
			int pageno = 1;
			List<Status> statuses = new LinkedList<Status>();

			int count = 100;
			//log.info("UserName :" + twitterHandleNames[i]);
			try {
				//twitter = reinitiateTwitter(twitter);
				Paging page;
				page = new Paging(pageno, count);
				statuses.addAll(twitter.getUserTimeline(twitterHandleNames[i], page));

			} catch (TwitterException e) {

				if(e.getStatusCode()==Config.Not_Found){
					log.error(twitterHandleNames[i] + " : " + e.getMessage());
					continue;
				}
				else if(e.getStatusCode()==Config.Too_Many_Requests){
					i--;
					twitter = reinitiateTwitter(twitter);
					log.error("Exception in TwitterTime API : " + Config.getStackTrace(e));
					continue;
				}else if(e.getStatusCode()==Config.Unauthorized){
					log.error(twitterHandleNames[i] + " : " + e.getMessage());
					continue;
				}
				
			}

			for (Status st : statuses) {
				if (!st.isRetweet()) {
					URLEntity[] uEn = st.getURLEntities();
					StringBuilder url = new StringBuilder();
					for (URLEntity urle : uEn) {
						if (urle.getURL() != null && !urle.getURL().isEmpty()) {
							url.append(urle.getExpandedURL());
						}
					}
					
					dbl.add(createTweetObject(twitterHandleNames[i], st,
							url.toString(),handleMap.get(twitterHandleNames[i])));
				}
			}

		}
		if (dbl != null && !dbl.isEmpty()) {
			populateTweetCollection(dbl);
		}
	}

	
	/*private void getTweetsUsingPaging(Twitter twitter, String[] userNames) {
		List<DBObject> dbl = new ArrayList<DBObject>();
		Map<String, Long> tweetIDMapUpdated = new HashMap<String, Long>();
		for (int i = 0; i < userNames.length; i++) {
			int pageno = 1;
			List<Status> statuses = new LinkedList<Status>();
			List<String> statusTextList = new LinkedList<String>();
			Long sinceId = tweetIDMap.get(userNames[i]);
			if (sinceId == null) {
				sinceId = 0l;
			}
			Long lastTweetID = 0l;
			Long total = 0l;
			int count = 100;
			while (true) {
				List<Status> statusePage = new LinkedList<Status>();
				System.out.println("UserName :" + userNames[i]);
				try {

					int size = statuses.size();
					Paging page;
					if (sinceId > 0) {
						page = new Paging(sinceId);
					} else {
						page = new Paging(pageno++, count);
					}
					statusePage.addAll(twitter.getUserTimeline(userNames[i],
							page));
					statuses.addAll(statusePage);
					total = total + statusePage.size();
					if (statuses.size() == size || statuses.size() < count) {
						break;
					}
					statusePage = new LinkedList<Status>();
					twitter = reinitiateTwitter(twitter);
				} catch (TwitterException e) {
					statusePage = new LinkedList<Status>();
					log.error("Exception in TwitterTime line api---"
							+ Config.getStackTrace(e));
					e.printStackTrace();
					break;
				}

			}
			for (Status st : statuses) {
				statusTextList.add(st.getText());
				URLEntity[] uEn = st.getURLEntities();
				for(URLEntity urle: uEn){
					System.out.println(urle.getURL());
				}
			}

			if (statuses.size() == 0) {
				lastTweetID = sinceId;
			} else {
				lastTweetID = statuses.get(0).getId();
				tweetIDMapUpdated.put(userNames[i], lastTweetID);
			}
			if (total > 0 && statusTextList.size() > 0) {
				dbl.add(createTweetObject(userNames[i], total, statusTextList));
			}
			total = 0l;
		}
		System.out.println(dbl.size());
		if (dbl != null && !dbl.isEmpty()) {
			populateTweetCollection(dbl);
		}
		if (tweetIDMapUpdated != null && !tweetIDMapUpdated.isEmpty()) {
			updateTweetIDMap(tweetIDMapUpdated);
		}
	}*/

	/**
	 * Re-Initiate twitter if Auth account rate limit exhausted
	 * 
	 * @param twitter
	 * @return
	 */
	private Twitter reinitiateTwitter(Twitter twitter) {
		try {

			set.incrementAndGet();
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey(handles.getProperty("ConsumerKeySet"+set.get()));
			cb.setOAuthConsumerSecret(handles.getProperty("ConsumerSecretSet"+set.get()));
			cb.setOAuthAccessToken(handles.getProperty("AccessTokenSet"+set.get()));
			cb.setOAuthAccessTokenSecret(handles.getProperty("AccessTokenSecretSet"+set.get()));
			cb.setUseSSL(true);
			twitter = new TwitterFactory(cb.build()).getInstance();
			
			/*Map<String, RateLimitStatus> rateMap = twitter.getRateLimitStatus();
			if (rateMap != null) {
				if (rateMap.containsKey("/users/lookup")) {
					RateLimitStatus rls = rateMap.get("/users/lookup");
					if (rls != null) {
						if (rls.getRemaining() > 2) {
							return twitter;
						}else{
							set.incrementAndGet();
							ConfigurationBuilder cb = new ConfigurationBuilder();
							cb.setOAuthConsumerKey(handles.getProperty("ConsumerKeySet"+set.get()));
							cb.setOAuthConsumerSecret(handles.getProperty("ConsumerSecretSet"+set.get()));
							cb.setOAuthAccessToken(handles.getProperty("AccessTokenSet"+set.get()));
							cb.setOAuthAccessTokenSecret(handles.getProperty("TokenSecretSet"+set.get()));
							cb.setUseSSL(true);
							twitter = new TwitterFactory(cb.build()).getInstance();
						}
					}
				}
			}*/
		} catch (Exception e) {
			
			log.error("Exception in TwitterTime line api---"
					+ Config.getStackTrace(e));
			e.printStackTrace();
		}
		return twitter;
	}

	/**
	 * Create db object for tweets
	 * 
	 * @param userName
	 * @param tweetCount
	 * @param posts
	 * @return
	 */
	private DBObject createTweetObject(String twitterName, Status st,
			String url, UserInfo userInfo) {

		DBObject tweet = new BasicDBObject();
		tweet.put("twHandle", twitterName);
		tweet.put("username", userInfo.getUsername());
		tweet.put("tweet", st.getText());
		tweet.put("url", url);
		tweet.put("rtwCount", st.getRetweetCount());
		tweet.put("tweetID", st.getId());
		tweet.put("twDate", st.getCreatedAt());
		tweet.put("isRetweet", st.isRetweet());
		tweet.put("center", userInfo.getCenter());
		Long timeStamp = DateTimeUtils.getHourSliceStamp() / 1000;
		tweet.put("ts", timeStamp);
		return tweet;
	}

	static class UserInfo{
		String center;
		String username;
		
		public UserInfo(String center, String username) {
			this.center = center;
			this.username = username;
		}

		public String getCenter() {
			return center;
		}

		public void setCenter(String center) {
			this.center = center;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		@Override
		public String toString() {
			return "UserInfo [center=" + center + ", username=" + username
					+ "]";
		}
		
	}
}
