package com.twitter.ibeat.stream;

import java.util.HashSet;

import twitter4j.Status;

/**
 * Created by Vibhav.Rohilla on 9/28/2015.
 */

public class TwitterCustomUtil {
	public static HashSet<String> userNamesSet;
	
	
/*	public final static HashSet userIdsSet;
	
	*//**
	 * Twitter accounts - NDTV - 37034483 TOI - 134758540 ABP News - 39240673 HT
	 * - 36327407 Breaking news - 6017542 BBC Breaking - 5402612 CNN Breaking -
	 * 428333 Reuters live - 15108702
	 *//*
	public final static long[] userIds = new long[] { 37034483, 134758540, 39240673, 36327407, 6017542, 5402612, 428333,
			15108702 };

	static {
		userIdsSet = new HashSet<Long>();
		userIdsSet.addAll(Arrays.asList(userIds));

		userNamesSet = new HashSet<String>();
		userNamesSet.add("ndtv");
		userNamesSet.add("timesofindia");
		userNamesSet.add("abpnewstv");
		userNamesSet.add("htTweets");
		userNamesSet.add("BreakingNews");
		userNamesSet.add("BBCBreaking");
		userNamesSet.add("cnnbrk");
		userNamesSet.add("ReutersLive");
	}*/

	public static HashSet<String> getUserNamesSet() {
		return userNamesSet;
	}


	public static void setUserNamesSet(HashSet<String> userNamesSet) {
		TwitterCustomUtil.userNamesSet = userNamesSet;
	}


	/**
	 * Method is called when a new status(tweet) is received in the stream. 
	 */
	public static void performStatusAction(Status status) {
		StatusManager statusManager = new StatusManager(status);
		Thread thread = new Thread(statusManager);
		thread.start();
	}
}
