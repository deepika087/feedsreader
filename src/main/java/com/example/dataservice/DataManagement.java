package com.example.dataservice;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class DataManagement {
	
	public static MongoClientURI uri;
	public static MongoClient client;
	
	public static MongoClient getMongoClient() {
		uri  = new MongoClientURI("mongodb://feedreader:feedreader@ds149335.mlab.com:49335/feedreader"); 
        client = new MongoClient(uri);
        return client;
	}
	
	public static String getMongoDB() {
		if (client == null) {
			client = DataManagement.getMongoClient();
		}
		return uri.getDatabase();
		//return client.getDatabase(uri.getDatabase());
	}

}	
