package com.twitter.ibeat.stream;

/**
 * Created by Vibhav.Rohilla on 9/28/2015.
 */
public interface StatusServiceApi {
    public void saveOrUpdateTweet(String status, String userName, long reTweetCount, boolean isReTweeted, String reTweetOf);
}
