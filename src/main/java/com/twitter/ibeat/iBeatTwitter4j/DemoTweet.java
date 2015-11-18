package com.twitter.ibeat.iBeatTwitter4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class DemoTweet {

	final String CONSUMER_KEY = "ARnijSvbkNXozvhFqU05zXbLk";
	final String CONSUMER_KEY_SECRET = "B6WAYhMSzFwy97esCOoFDrVg4UqKPfOslpCr74paMAKWiZhz6U";
	final String ACCESS_TOKEN = "104416164-0RGipWPka12h3lkvgM9wra1a8m6ZhaDF3t8IMmwX";
	final String ACCESS_TOKEN_SECRET = "tgwcEySgLOvJcbRHirtejxdm2lxAdZecUDnwuwJUDCWla";

	public static void main(String[] args) {
		DemoTweet tweet = new DemoTweet();
		// tweet.start();
		tweet.testPostingToTwitter();

	}

	private void testPostingToTwitter() {
		Twitter twitter = TwitterFactory.getSingleton();
		String message="\"A Visit to Transylvania\" by Euromaxx: Lifestyle Europe (DW) \n http://bit.ly/1cHB7MH";
		Status status;
		try {
			status = twitter.updateStatus(message);
			System.out.println("RetweetId : "+status.getCurrentUserRetweetId());
			System.out.println(status.getText());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
	}

	private void start() throws TwitterException, IOException {
		new TwitterFactory();
		Twitter twitter = TwitterFactory.getSingleton();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);

		RequestToken token = twitter.getOAuthRequestToken();
		System.out.println("Authorization URL: \n"
				+ token.getAuthorizationURL());

		AccessToken accessToken = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			try {
				System.out.print("Input PIN here: ");
				String pin = br.readLine();

				accessToken = twitter.getOAuthAccessToken(token, pin);

			} catch (TwitterException te) {

				System.out.println("Failed to get access token, caused by: "
						+ te.getMessage());

				System.out.println("Retry input PIN");

			}
		}

		System.out.println("Access Token: " + accessToken.getToken());
		System.out.println("Access Token Secret: "
				+ accessToken.getTokenSecret());

		twitter.updateStatus("hi.. im updating this using Namex Tweet for Demo");

	}
}
