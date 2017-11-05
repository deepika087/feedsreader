package com.example.dataservice;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
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
		
		//1. Insert article directly in articles collection but hold it to check if corresponding feed is there
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> artcile_collection = db.getCollection(DataConstants.ARTICLES_COLLECTION);
		
		Document doc_to_be_inserted = new Document("content", article_body);
		
		//2. Associate article in Feed
		MongoCollection<Document> feeds_collection = db.getCollection(DataConstants.FEEDS_COLLECTION);
		Document findQuery = new Document("feedname", new Document("$eq", feedname));
		MongoCursor<Document> cursor = feeds_collection.find(findQuery).iterator();
		try {
            while (cursor.hasNext()) {
            	artcile_collection.insertOne(  doc_to_be_inserted );
        		
        		String article_id = doc_to_be_inserted.getObjectId("_id").toString();
        		logger.info("ID of the ID just created" + article_id);
        		
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
		throw new FeedReaderException("something went wrong while attaching article to feed.It is possible that feed with this name is not present " + feedname);
	}
	
	public String subscribeFeed(final String feedName, final String userName) throws FeedReaderException {
		
		logger.info("Reaching here with to subscribe" + userName + " to feedName" + feedName);
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> user_collection = db.getCollection(DataConstants.USER_COLLECTION);
		
		Document findQuery = new Document("username", new Document("$eq", userName));
		MongoCursor<Document> cursor = user_collection.find(findQuery).iterator();
		try {
            while (cursor.hasNext()) {
            	
                Document doc = cursor.next();
                List<String> feed_ids =  (List<String>)doc.get("feedIds");
                if (feed_ids == null) {
                	feed_ids = new ArrayList<String>();
                }
                else {
                	List<String> feed_names = getFeedNames(feed_ids);
                	if (feed_names.contains(feedName)) {
                    	throw new FeedReaderException("This user is already subscribed ! !");
                    }
                }
                
                String result_feed_id = getFeedId(feedName);
                feed_ids.add(result_feed_id);
                
                logger.info("List of feed_list formed: " + feed_ids);
                Document feed_subscription = new Document();
                feed_subscription.append("$set", new Document("feedIds", feed_ids));
                user_collection.updateOne(findQuery, feed_subscription); 
                logger.info("Update Mongodb with subscription list. Please check");
                return "";
            }
        } finally {
            cursor.close();
        }
		throw new FeedReaderException("User is not present in the DB " + userName);
	}
	
	public String unsubscribeFeed(final String feedName, final String userName) throws FeedReaderException {
		
		logger.info("Reaching here with to unsubscribe" + userName + " from feedName" + feedName);
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> user_collection = db.getCollection(DataConstants.USER_COLLECTION);
		
		Document findQuery = new Document("username", new Document("$eq", userName));
		MongoCursor<Document> cursor = user_collection.find(findQuery).iterator();
		try {
            while (cursor.hasNext()) {
            	
                Document doc = cursor.next();
                List<String> feed_ids =  (List<String>)doc.get("feedIds"); //List of Ids
                if (feed_ids == null) {
                	feed_ids = new ArrayList<String>();
                } else {
                	List<String> feed_names = getFeedNames(feed_ids);
                	if (!feed_names.contains(feedName)) {
                    	throw new FeedReaderException("This user is not subscribed to this feed ! !");
                    }
                }
                
                String result_feed_id = getFeedId(feedName);
                feed_ids.remove(result_feed_id);
                
                logger.info("List of feed_list formed: " + feed_ids);
                Document feed_subscription = new Document();
                feed_subscription.append("$set", new Document("feedIds", feed_ids));
                user_collection.updateOne(findQuery, feed_subscription); 
                logger.info("Update Mongodb with subscription list. Please check");
                return "";
            }
        } finally {
            cursor.close();
        }
		throw new FeedReaderException("User not present in DB");
	}
	
	//This function takes feed ids as input and returns feed names as output
	private List<String> getFeedNames(List<String> feed_ids) {
		
		List<String> feed_names = new ArrayList<>();
		for (String feed_id : feed_ids) {
			feed_names.add(getFeedName(feed_id));
		}
		return feed_names;
		
	}
	
	private String getFeedName(String feed_id) {
		
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> feed_collection = db.getCollection(DataConstants.FEEDS_COLLECTION);
		
		Document query = new Document();
	    query.put("_id", new ObjectId(feed_id));

	    MongoCursor<Document> cursor = feed_collection.find(query).iterator();
	    try {
            while (cursor.hasNext()) {
            	
                Document doc = cursor.next();
                return doc.getString("feedname");
            }
        } finally {
            cursor.close();
        }
	    return "";
	}
	
	private String getFeedId(String feedname) throws FeedReaderException {
		
		MongoDatabase db = DataManagement.getMongoDB();
		MongoCollection<Document> feed_collection = db.getCollection(DataConstants.FEEDS_COLLECTION);
		
		Document query = new Document();
	    query.put("feedname", feedname);
	    MongoCursor<Document> cursor = feed_collection.find(query).iterator();
	    try {
            while (cursor.hasNext()) {
            	
                Document doc = cursor.next();
                return doc.get("_id").toString();
            }
        } finally {
            cursor.close();
        }
		throw new FeedReaderException("Feed is not present in the DB");
	}
}	
