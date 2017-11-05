package com.example.dataservice;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.constants.DataConstants;
import com.example.exceptions.FeedReaderException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class DataManagement {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public static MongoClientURI uri;
	public static MongoClient client;
	
	public static MongoClient getMongoClient() {
		uri  = new MongoClientURI("mongodb://feedreader:feedreader@ds149335.mlab.com:49335/feedreader"); 
        client = new MongoClient(uri);
        return client;
	}
	
	public static MongoDatabase getMongoDB() {
		if (client == null) {
			client = DataManagement.getMongoClient();
		}
		System.out.println("Database found deepika: " + uri.getDatabase()); 
		return client.getDatabase(uri.getDatabase());
	}

	public String createUser(final String userName) throws FeedReaderException{
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> user_collection = db.getCollection(DataConstants.USER_COLLECTION);
		// 1. Find if it already exists
		Document findQuery = new Document("username", new Document("$eq",userName));
		MongoCursor<Document> cursor = user_collection.find(findQuery).iterator();
		
		try {
		        while (cursor.hasNext()) {
		            throw new FeedReaderException("User with this name already present in database");
		        }
		} finally {
		    cursor.close();
		}
		
		// 2. Create a user 
		user_collection.insertOne( new Document("username", userName) );
		
		//3. Fetch the ID
		cursor = user_collection.find(findQuery).iterator();
		try {
	        while (cursor.hasNext()) {
	        	Document doc = cursor.next();
	        	logger.info("DUMPPED DOCUMENT: " + doc);
	        	System.out.println("DUMPPED DOCUMENT: " + doc);
	        }
		} finally {
		    cursor.close();
		}
		return userName;
	}
}	
