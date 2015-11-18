package com.twitter.ibeat.iBeatSocial;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

/**
 * Class to access user specific data using Spring Social Twitter API
 * 
 * @see <a
 *      href="http://docs.spring.io/spring-social-twitter/docs/1.1.0.RELEASE/reference/htmlsingle/">http://docs.spring.io/spring-social-twitter/docs/1.1.0.RELEASE/reference/htmlsingle/</a>
 */
public class TwitterOperation {

	final static Logger logger = Logger.getLogger(TwitterOperation.class);
			
	private Twitter twitterObj;

	public void getUserProfileData() {

		TwitterProfile profile = twitterObj.userOperations().getUserProfile("ibeatuser1");
		logger.info(profile.getProfileUrl());

		String profileId = twitterObj.userOperations().getScreenName();
		logger.info("ScreenName " + profileId);
	}
	
	//Fetch latest 20 tweets
	public void getTweets(){
		String screenName="rajeshkalra";
		//List<Tweet> tweetFeed=twitterObj.timelineOperations().getUserTimeline(screenName, 200);
		//logger.info(tweetFeed.size());
		
		/*for (Tweet tweet : tweets) {
			logger.info(tweet.getText());
		}
		List<Tweet> tweetsUser = twitterObj.timelineOperations().getMentions(1);
		logger.info(tweetsUser);*/
		
		List<Tweet> totalTweets = new ArrayList<Tweet>();

		int pageSize = 200;
		int page = 1;
		long sinceId = 0;
		long maxId = 0;
		while (true) {
			try {
				List<Tweet> tweets;
				if (page++ == 1) {
					tweets = twitterObj.timelineOperations().getUserTimeline(
							screenName, pageSize);
				} else {
					tweets = twitterObj.timelineOperations().getUserTimeline(
							screenName, pageSize, sinceId, maxId);
				}

				totalTweets.addAll(tweets);

				sinceId = tweets.get(0).getId();
				int lastIndex = tweets.size() - 1;
				maxId = tweets.get(lastIndex).getId();
			} catch (Exception e) {
				logger.error(e.getMessage());
				break;
			}
		}

		logger.info("Total Tweets : " + totalTweets.size());
	}

	public Twitter getTwitterObj() {
		return twitterObj;
	}

	public void setTwitterObj(Twitter twitterObj) {
		this.twitterObj = twitterObj;
	}
}
