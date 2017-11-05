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
                new_feed.setId(doc.get("_id").toString());
                feed_data.add(new_feed);  
            }
        } finally {
            cursor.close();
        }
		return feed_data;
	}
	
	public String createArticle(final String feedname, final String article_body) throws FeedReaderException{
		
		logger.info("Reaching here with feedname" + feedname + " and artcile_body" + article_body);
		//1. Insertion directly in articles collection 
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> artcile_collection = db.getCollection(DataConstants.ARTICLES_COLLECTION);
		
		Document doc_to_be_inserted = new Document("content", article_body);
		
		artcile_collection.insertOne(  doc_to_be_inserted );
		
		String article_id = doc_to_be_inserted.getObjectId("_id").toString();
		logger.info("ID of the ID just created" + article_id);
		
		//2. Associate article in Feed
		MongoCollection<Document> feeds_collection = db.getCollection(DataConstants.FEEDS_COLLECTION);
		Document findQuery = new Document("feedname", new Document("$eq", feedname));
		MongoCursor<Document> cursor = feeds_collection.find(findQuery).iterator();
		try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                List<String> old_list =  (List<String>)doc.get("articleIds");
                if (old_list == null) {
                	old_list = new ArrayList<String>();
                }
                old_list.add(article_id);
                
                logger.info("List of article formed: " + old_list);
                Document push_articles = new Document();
                push_articles.append("$set", new Document("articleIds", old_list));
                feeds_collection.updateOne(findQuery, push_articles); 
                logger.info("Update Mongodb. Please check");
                return article_id;
            }
        } finally {
            cursor.close();
        }
		throw new FeedReaderException("something went wrong while attaching article to feed " + feedname);
		
	}
	
}	
