package com.twitter.ibeat.stream;

import twitter4j.Status;

/**
 * Created by Vibhav.Rohilla on 9/28/2015.
 */

/**
 * Class to Call the service that stores the stream data to DB (raw data from
 * twitter)
 * 
 *
 */
public class StatusManager implements Runnable {
	private Status status;
	StatusServiceApi statusService = new StatusService();

	public StatusManager(Status status) {
		this.status = status;
	}

	/**
	 * Every time a new entry is arrives in the stream, the data is cleaned and
	 * pushed to DB through a separate Thread so that application does not halt
	 * till the time insertion operation is carried.
	 */
	@Override
	public void run() {
		String screenName = status.getUser().getScreenName();
		if (TwitterCustomUtil.userNamesSet != null && TwitterCustomUtil.userNamesSet.contains(screenName)) {
			statusService.saveOrUpdateTweet(status.getText(), status.getUser().getScreenName(),
					status.getRetweetCount(), status.isRetweet(), null);
		} else {
			String tweetText = "";
			String reTweetUserScreenName = "None";
			if (status.getRetweetedStatus() == null) {
				tweetText = status.getText();
			} else {
				tweetText = status.getRetweetedStatus().getText();
				if (status.getRetweetedStatus().getUser() != null) {
					reTweetUserScreenName = status.getRetweetedStatus().getUser().getScreenName();
				}
			}
			statusService.saveOrUpdateTweet(tweetText, status.getUser().getScreenName(), status.getRetweetCount(),
					status.isRetweet(), reTweetUserScreenName);

		}
	}
}
