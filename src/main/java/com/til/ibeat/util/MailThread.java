package com.til.ibeat.util;

import java.util.Date;

import com.til.ibeat.script.Config;
import com.til.ibeat.script.SendTopArticleHTML;
import org.apache.log4j.Logger;


public class MailThread implements Runnable{
	private static Logger log = Logger.getLogger(MailThread.class);

	private String[] to;
	private String from;
	private String subject;
	private String body;
	private String hostName;
	private String senderName;
	private String[] cc;
	private String[] bcc;
	private boolean appendDateToSubject = true;
	public MailThread(String[] to, String from,String senderName, String[] cc, String subject, String body,String hostName,String[] bcc,boolean appendDateToSubject) {
		this.to =  to;
		this.from =  from;
		this.subject = subject;
		this.body = body;
		this.hostName = hostName;
		this.senderName = senderName;
		this.cc = cc;
		this.bcc = bcc;
		this.appendDateToSubject = appendDateToSubject;
	}
	@Override
	public void run() {
        try {
            SendTopArticleHTML svm = new SendTopArticleHTML();
            svm.send(to, from, senderName, cc, bcc, (appendDateToSubject) ? subject + "  " + new Date() : subject, body, hostName);
        } catch (Exception e) {
            log.error(Config.getStackTrace(e));
        }
    }

}
