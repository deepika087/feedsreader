package com.example.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.constants.DataConstants;
import com.example.datamodels.Feed;
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

	public String createUser(final String name) throws FeedReaderException{
		return createResource(name, DataConstants.USER_COLLECTION, "username");
	}

	private String createResource(
			final String name, 
			final String collectionName,
			final String collection_key) throws FeedReaderException {
		
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> corresponding_collection = db.getCollection(collectionName);
		
		// 1. Find if the resource already exists
		Document findQuery = new Document(collection_key, new Document("$eq", name));
		MongoCursor<Document> cursor = corresponding_collection.find(findQuery).iterator();
		
		try {
		        while (cursor.hasNext()) {
		            throw new FeedReaderException("key already present in database");
		        }
		} finally {
		    cursor.close();
		}
		
		// 2. Create a user/Article/Feed
		corresponding_collection.insertOne( new Document(collection_key, name) );
		
		//3. Fetch the ID
		cursor = corresponding_collection.find(findQuery).iterator();
		try {
	        while (cursor.hasNext()) {
	        	Document doc = cursor.next();
	        	logger.info("DUMPPED DOCUMENT: " + doc);
	        	return doc.get("_id").toString();
	        }
		} finally {
		    cursor.close();
		}
		return name;
	}
	
	public String createFeed(final String name) throws FeedReaderException {
		return createResource(name, DataConstants.FEEDS_COLLECTION, "feedname");
		
	}
	
	public List<Feed> getAllFeeds() throws FeedReaderException {
		
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> corresponding_collection = db.getCollection(DataConstants.FEEDS_COLLECTION);
		
		MongoCursor<Document> cursor = corresponding_collection.find().iterator();
		final List<Feed> feed_data = new ArrayList<Feed>();
		try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                
                Feed new_feed = new Feed(doc.getString("feedname"), (List<String>)doc.get("articleIds"));
                feed_data.add(new_feed);  
            }
        } finally {
            cursor.close();
        }
		return feed_data;
	}
	
}	
