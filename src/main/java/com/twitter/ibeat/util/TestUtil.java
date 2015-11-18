package com.twitter.ibeat.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;

public class TestUtil {
	private MongoClient mongoHistory;
	private MongoClient mongoLocal;
	
	public TestUtil() throws UnknownHostException {
		mongoHistory = new MongoClient(Config.HOST_HISTORICAL,
				MongoClientOptions.builder().connectionsPerHost(10)
				.threadsAllowedToBlockForConnectionMultiplier(15)
				.connectTimeout(5000).writeConcern(WriteConcern.ACKNOWLEDGED)
				.build());
		
		mongoLocal= new MongoClient("127.0.0.1", MongoClientOptions
				.builder().writeConcern(WriteConcern.ACKNOWLEDGED).build());
	}

	private void insertTwitterData() {
		BufferedReader br = null;
		String currentLine;
		List<DBObject> centerList = new ArrayList<DBObject>();
		String filepath="E:\\git\\iBeatTwitter\\src\\main\\resources\\Twitter.csv";
		
		//DB busineesDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DB busineesDB=mongoLocal.getDB(Config.DBNAME_BUSINESS);
		
		// CenterWise Collection
		DBCollection centerCollection = busineesDB
				.getCollection(Config.HANDLES_CENTER_COLLECTION);
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));

			int count=0;
			while ((currentLine = br.readLine()) != null) {
				String[] inputArr = currentLine.split(",");
				
				if (inputArr.length >= 5) {
					String username = inputArr[0];
					String location= inputArr[1];
					String email = inputArr[2];
					String twitterHandle = inputArr[3];
					String center= inputArr[4];
					
					// username
					if (Config.chkNull(username)) {
						username = "";
					} else {
						username = username.trim();
						username = WordUtils.capitalizeFully(username);
					}

					// email
					if (Config.chkNull(email)) {
						email = "";
					} else {
						email = email.trim();
					}

					// center
					if (Config.chkNull(center)) {
						continue;
					} else {
						center = center.trim();
						center = center.toLowerCase();
					}

					// location
					if (Config.chkNull(location)) {
						location = center;
					} else {
						location=location.trim();
						location = location.toLowerCase();
					}
					
					//twitter Handle
					if(Config.chkNull(twitterHandle)){
						System.out.println(username);
						count++;
						continue;
					}else{
						twitterHandle=twitterHandle.trim();
						
						if(twitterHandle.startsWith("@")){
							twitterHandle=twitterHandle.substring(1, twitterHandle.length()-1);
						}
					}
					
					DBObject obj = new BasicDBObject();
					obj.put("username", username);
					obj.put("twHandle", twitterHandle);
					obj.put("email", email);
					obj.put("location", location);
					obj.put("center", center);

					centerList.add(obj);
					
					try {
						centerCollection.insert(obj);
					} catch (Exception e) {
					}
				}else{
					System.out.println(currentLine);
				}
			}

			System.out.println("Twitter Handle Missing Count : " + count);
			//centerCollection.insert(centerList);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TestUtil util = null;
		try {
			util = new TestUtil();
			//util.insertTwitterData();
			//util.printHandle(4);
			util.printFaultyHandle("404");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

	private void printFaultyHandle(String s) {
		BufferedReader br = null;
		String currentLine;
		List<String> centerList = new ArrayList<String>();
		String filepath="E:\\dump\\twitter404.log";
		
		//DB busineesDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		DB busineesDB=mongoLocal.getDB(Config.DBNAME_BUSINESS);
		
		// CenterWise Collection
		DBCollection centerCollection = busineesDB
				.getCollection(Config.HANDLES_CENTER_COLLECTION);
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));

			int count=0;
			while ((currentLine = br.readLine()) != null) {
				String[] inputArr = currentLine.split(":");
				
				if (inputArr.length >= 2) {
					String handle = inputArr[0];
					
					// email
					if (Config.chkNull(handle)) {
						continue;
					} else {
						handle = handle.trim();
					}

					StringBuilder builder=new StringBuilder();
					DBObject obj=centerCollection.findOne(new BasicDBObject("twHandle",handle));
					if (obj != null) {
						String username = (String) obj.get("username");
						builder.append(username + ",");
						builder.append(handle + ",");

						String email = "";
						if (obj.get("email") != null) {
							email = (String) obj.get("email");
						}
						builder.append(email + ",");

						String location = (String) obj.get("location");
						builder.append(location + ",");
						String center = (String) obj.get("center");
						builder.append(center + ",");
					}
					centerList.add(builder.toString());
					System.out.println(builder);
				}else{
					System.out.println(currentLine);
				}
			}
			System.out.println(centerList);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printHandle(int n) {
		DB busineesDB = mongoHistory.getDB(Config.DBNAME_BUSINESS);
		//DB busineesDB=mongoLocal.getDB(Config.DBNAME_BUSINESS);
		
		// CenterWise Collection
		DBCollection centerCollection = busineesDB.getCollection(Config.HANDLES_CENTER_COLLECTION);
		
		DBCursor cursor=centerCollection.find();
		
		Set<String> handleSet=new HashSet<String>();
		
		while(cursor.hasNext()){
			handleSet.add((String) cursor.next().get("twHandle"));
		}
		
		int partition = handleSet.size() / n;
		
		StringBuilder builder=new StringBuilder();
		int counter=1;
		int loop=1;
		for (String handle : handleSet) {
			
			if (counter == partition) {
				System.out.println(loop++);
				System.out.println(builder);
				builder = new StringBuilder();
				counter = 0;
			}
			
			builder.append(handle+",");
			counter++;
		}
		
		if(builder.length()!=0){
			System.out.println(loop++);
			System.out.println(builder);
		}
		System.out.println();
	}
}
class Cache{
	int a;
	int b;
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	
}