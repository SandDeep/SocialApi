package com.twitter.ibeat.stream;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import twitter4j.DirectMessage;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Vibhav.Rohilla on 9/28/2015.
 */
public class TwitterStatusHandler {
	Properties handles = null;
	String[] twitterHandleNames = null;
	static TwitterStream twitterStream = null;
	private AtomicInteger set = new AtomicInteger(1);
	private static long[] userIds = null;
	private static Logger log = Logger.getLogger(TwitterStatusHandler.class);

	public TwitterStatusHandler(String twitterHandles) throws IOException {
		FileInputStream file = new FileInputStream(twitterHandles);
		handles.load(file);
		file.close();
		String names = handles.getProperty("handles");

		if (names != null && !names.isEmpty()) {
			twitterHandleNames = names.split(";");
			TwitterCustomUtil.setUserNamesSet(new HashSet<String>(Arrays.asList(twitterHandleNames)));
		}

		String userIdsLong = handles.getProperty("handlesLong");
		if (userIdsLong != null && !userIdsLong.isEmpty()) {
			String[] userIdsLongSplit = userIdsLong.split(";");
			userIds = new long[userIdsLongSplit.length];

			for (int index = 0; index < userIdsLongSplit.length; index++) {
				userIds[index] = Long.parseLong(userIdsLongSplit[index]);
			}

		}

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(handles.getProperty("ConsumerKeySet" + set.get()));
		cb.setOAuthConsumerSecret(handles.getProperty("ConsumerSecretSet" + set.get()));
		cb.setOAuthAccessToken(handles.getProperty("AccessTokenSet" + set.get()));
		cb.setOAuthAccessTokenSecret(handles.getProperty("AccessTokenSecretSet" + set.get()));

		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

	}

	private void processTweets() {
		UserStreamListener listener = new UserStreamListener() {
			@Override
			public void onStatus(Status status) {
				if (status == null) {
					return;
				}
				System.out.println("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
				TwitterCustomUtil.performStatusAction(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onDeletionNotice(long directMessageId, long userId) {
				System.out.println("Got a direct message deletion notice id:" + directMessageId);
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onFriendList(long[] friendIds) {
				System.out.print("onFriendList");
				for (long friendId : friendIds) {
					System.out.print(" " + friendId);
				}
				System.out.println();
			}

			@Override
			public void onFavorite(User source, User target, Status favoritedStatus) {
				System.out.println("onFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName()
						+ " @" + favoritedStatus.getUser().getScreenName() + " - " + favoritedStatus.getText());
			}

			@Override
			public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
				System.out.println(
						"onUnFavorite source:@" + source.getScreenName() + " target:@" + target.getScreenName() + " @"
								+ unfavoritedStatus.getUser().getScreenName() + " - " + unfavoritedStatus.getText());
			}

			@Override
			public void onFollow(User source, User followedUser) {
				System.out.println(
						"onFollow source:@" + source.getScreenName() + " target:@" + followedUser.getScreenName());
			}

			@Override
			public void onDirectMessage(DirectMessage directMessage) {
				System.out.println("onDirectMessage text:" + directMessage.getText());
			}

			@Override
			public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
				System.out.println("onUserListMemberAddition added member:@" + addedMember.getScreenName()
						+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
				System.out.println("onUserListMemberDeleted deleted member:@" + deletedMember.getScreenName()
						+ " listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
				System.out.println("onUserListSubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
						+ listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
				System.out.println("onUserListUnsubscribed subscriber:@" + subscriber.getScreenName() + " listOwner:@"
						+ listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListCreation(User listOwner, UserList list) {
				System.out.println(
						"onUserListCreated listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListUpdate(User listOwner, UserList list) {
				System.out.println(
						"onUserListUpdated listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserListDeletion(User listOwner, UserList list) {
				System.out.println(
						"onUserListDestroyed listOwner:@" + listOwner.getScreenName() + " list:" + list.getName());
			}

			@Override
			public void onUserProfileUpdate(User updatedUser) {
				System.out.println("onUserProfileUpdated user:@" + updatedUser.getScreenName());
			}

			@Override
			public void onBlock(User source, User blockedUser) {
				System.out.println(
						"onBlock source:@" + source.getScreenName() + " target:@" + blockedUser.getScreenName());
			}

			@Override
			public void onUnblock(User source, User unblockedUser) {
				System.out.println(
						"onUnblock source:@" + source.getScreenName() + " target:@" + unblockedUser.getScreenName());
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
				System.out.println("onException:" + ex.getMessage());
			}

			@Override
			public void onFavoritedRetweet(User arg0, User arg1, Status arg2) {

			}

			@Override
			public void onQuotedTweet(User arg0, User arg1, Status arg2) {

			}

			@Override
			public void onRetweetedRetweet(User arg0, User arg1, Status arg2) {
			}

			@Override
			public void onUnfollow(User arg0, User arg1) {
			}

			@Override
			public void onUserDeletion(long arg0) {
			}

			@Override
			public void onUserSuspension(long arg0) {
			}
		};

		FilterQuery queryObj = new FilterQuery();
		twitterStream.addListener(listener);
		/*
		 * Use Track to get tweets that contain the words mentioned
		 */
		// queryObj.track(new String[]{"#qwertyqwerty"});

		/*
		 * Use Follow to get tweets from a list of users. Pass a long type array
		 * having user ids.
		 */

		/*
		 * Twitter accounts - NDTV - 37034483 TOI - 134758540 ABP News -
		 * 39240673 HT - 36327407 Breaking news - 6017542 BBC Breaking - 5402612
		 * CNN Breaking - 428333 Reuters live - 15108702
		 */
		queryObj.follow(userIds);
		// twitterStream.user(new String[]{"3711546980"});
		twitterStream.filter(queryObj);

	}

	public static void main(String[] args) throws IOException {
		/*
		 * ConfigurationBuilder cb = new ConfigurationBuilder();
		 * cb.setDebugEnabled(true);
		 * cb.setOAuthConsumerKey("Qo0vGW0ZKWzgyqkybr6trBnTc");
		 * cb.setOAuthConsumerSecret(
		 * "r57IzOSglCwrGUj9s41VTwrl2RM0d61dXmNORGi76qTPcuppFU");
		 * cb.setOAuthAccessToken(
		 * "104416164-0RGipWPka12h3lkvgM9wra1a8m6ZhaDF3t8IMmwX");
		 * cb.setOAuthAccessTokenSecret(
		 * "tgwcEySgLOvJcbRHirtejxdm2lxAdZecUDnwuwJUDCWla");
		 */
		if (args.length < 1) {
			log.error("Insufficient arguments : Usage: TwitterTimeLine file path");
			System.exit(0);
		}
		String twitterHandles = args[0];
		TwitterStatusHandler twitterHandler = new TwitterStatusHandler(twitterHandles);
		twitterHandler.processTweets();
	}
}
