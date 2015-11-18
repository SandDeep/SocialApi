package com.twitter.ibeat.stream;

import com.mongodb.*;
import com.twitter.ibeat.util.Config;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by Vibhav.Rohilla on 9/28/2015.
 */

/**
 * Class to initiate mongoDB Insertion 
 */
public class StatusService implements StatusServiceApi {
	private static MongoClient mongoClient;
	private static DB dbObject;
	private static DBCollection tweetCollection;

	/**
	 * Static block to initialize Mongo DB Connection variables
	 */
	static {
		try {
			mongoClient = new MongoClient(Config.HOST_SCRIPT,
					MongoClientOptions.builder().connectionsPerHost(10).threadsAllowedToBlockForConnectionMultiplier(15)
							.connectTimeout(5000).writeConcern(WriteConcern.NORMAL).build());
			dbObject = mongoClient.getDB(Config.DBNAME_TWITTER_DB);
			tweetCollection = dbObject.getCollection(Config.TWITTER_LOG);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method creates a BasicDBObject and insert the data in mongoDB. Params inserted are - 
	 * 	1. Status
	 * 	2. Re-tweet Count
	 *  3. in Re-Tweeted
	 *  4. ReTweet of User
	 *  5. timeStamp
	 */
	@Override
	public void saveOrUpdateTweet(String status, String userName, long reTweetCount, boolean isReTweeted,
			String reTweetOf) {

		BasicDBList tweetList = new BasicDBList();
		tweetList.add(status);
		DBObject insertObj = new BasicDBObject("username", userName).
				append("status", tweetList).
				append("reTweetCount", reTweetCount).
				append("isReTweeted", isReTweeted).
				append("reTweetOf", reTweetOf).
				append("timeStamp", (new Date()).getTime());
		tweetCollection.insert(insertObj);
	}

}
