package com.twitter.ibeat.iBeatSocial;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class to access spring Application.
 *
 */
public class App 
{
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args )
    {
       ApplicationContext context=new ClassPathXmlApplicationContext("Social-Module.xml");
       
       TwitterOperation operation=(TwitterOperation) context.getBean("twitterOpr");
       operation.getUserProfileData();
       operation.getTweets();
       
       FacebookOperation facebookOperation=(FacebookOperation) context.getBean("facebookOpr");
       facebookOperation.getUserProfileData();
       facebookOperation.friendsInformation();
       
      ((AbstractApplicationContext)context).registerShutdownHook();
    }
}
