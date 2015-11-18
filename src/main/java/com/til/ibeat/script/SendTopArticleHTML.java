package com.til.ibeat.script;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendTopArticleHTML {
	private static Logger log = Logger.getLogger(SendTopArticleHTML.class);


	public void send(String[] to, String from,String senderName,String[] cc,String[] bcc, String subject, String body,String hostName) {
		boolean sessionDebug = false;
		Properties props = System.getProperties();
		props.put("mail.smtp.host", hostName);
		props.put("mail.smtp.port", "25");
		props.put("mail.transport.protocol", "smtp");
		
		
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(sessionDebug);
		
		try {
			// Instantiate a new MimeMessage and fill it with the required information.
			Message msg = new MimeMessage(session);
			try {
				msg.setFrom(new InternetAddress(from,senderName));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//InternetAddress[] address = {new InternetAddress(to)};
			InternetAddress[] addressTo = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++)
			{
			    addressTo[i] = new InternetAddress(to[i]);
			}
			
			try {
				InternetAddress[] addressCC = new InternetAddress[cc.length];
				for (int i = 0; i < cc.length; i++)
				{
					addressCC[i] = new InternetAddress(cc[i]);
				}
				msg.setRecipients(Message.RecipientType.CC, addressCC);
			} catch (Exception e) {
				log.debug("CC is null");
			}
			
			try {
				InternetAddress[] addressBCC = new InternetAddress[bcc.length];
				for (int i = 0; i < bcc.length; i++)
				{
					addressBCC[i] = new InternetAddress(bcc[i]);
				}
				msg.setRecipients(Message.RecipientType.BCC, addressBCC);
			} catch (Exception e) {
				log.debug("BCC is null");
			}
			msg.setHeader("Content-Type", "text/html; charset=ISO-8859-1");
			msg.setRecipients(Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			//msg.setText(mailStr);
			msg.setContent(body,"text/html; charset=ISO-8859-1");
			
			Transport.send(msg);
		}catch (MessagingException mex) {
			mex.printStackTrace();
			}

	}
	
	/*public static void main(String[] args) {
		SendTopArticleHTML svm = new SendTopArticleHTML();
		String[] recipients = {"virendra.agarwal@indiatimes.co.in"
				, "virendra.agarwal@indiatimes.co.in"
				, "virendra.agarwal@indiatimes.co.in"
				}; 
		svm.send(recipients, "no-reply@indiatimes.co.in",null, "Top Article Data: " + new Date(), "Please find attached the report");
	}*/
	
}
