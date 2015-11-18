package com.twitter.ibeat.iBeatSocial;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class FacebookOperation {
	final static Logger logger = Logger.getLogger(FacebookOperation.class);

	Facebook facebook;

	public void getUserProfileData(){
		User userProfile=facebook.userOperations().getUserProfile();
		logger.info(userProfile.getName() + " " + userProfile.getLastName());
		
		User user1=facebook.userOperations().getUserProfile("4");
		logger.info(user1.getName() + " " + user1.getLastName());
	}
	
	public void friendsInformation(){
		List<Page> friendsIds=facebook.likeOperations().getMusic();
		logger.info(friendsIds.size());
		//User friend=facebook.userOperations().getUserProfile(friendsIds.get(4));
		//logger.info(friend.getName() + " " + friend.getLastName());
	}

	@SuppressWarnings("unchecked")
	public void publicPagesFeed(){
		String ownerId="TimesofIndia"/*"rajnetikbuckchod"*/;
		//Max Limit Allowed : 100
		PagingParameters firstPage = new PagingParameters(100, null,/*null,null*/ 1444195800L, 1444199400L);

		Collection<Post> posts=this.facebook.feedOperations().getPosts(ownerId, firstPage);
		logger.info("Total Feeds : " + posts.size());
		for (Post post : posts) {
			logger.info(post.getId() + " : " + post.getCreatedTime() + " ---- " + post.getMessage());
		}

		Page page=facebook.pageOperations().getPage(ownerId);
		List<Page> pages=facebook.likeOperations().getPagesLiked(ownerId);
		logger.info(page);
		
		final String FQL_GET_FRIENDS = "SELECT uid, name FROM user WHERE uid IN(SELECT uid2 FROM friend WHERE uid1 = me())";
		MultiValueMap<String, String> params=new LinkedMultiValueMap<String, String>();
		params.set("q", FQL_GET_FRIENDS);
		
		//FQL
		Map<String, Object> resultSet=(Map<String, Object>)facebook.fetchObject("fql",Map.class,params);
		logger.info(resultSet);
		//Post post=facebook.restOperations().getForObject("https://graph.facebook.com/102527030797_10153675197385798", responseType);
		
	}
	public Facebook getFacebook() {
		return facebook;
	}

	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}
}
