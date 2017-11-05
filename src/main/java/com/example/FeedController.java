package com.example;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.datamodels.Feed;
import com.example.datamodels.Messages;
import com.example.dataservice.DataManagement;
import com.example.exceptions.FeedReaderException;

@Controller
@RequestMapping("/feed")
public class FeedController {
	
private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DataManagement dataManagement;
	
	@RequestMapping(value = "/{feedname}", method = RequestMethod.POST)
	public @ResponseBody Messages createFeed(@PathVariable String feedname) {
		
		logger.info("Request for feedname: " + feedname);
		try {
			String id_created = dataManagement.createFeed(feedname);
			return new Messages(200, "Feed is created with unique id: " + id_created);
		} catch (FeedReaderException ex) {
			return new Messages(404, "Feed with this name is already present");
		}	
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Feed> getFeeds() {
		
		logger.info("Fetching all feeds...." );
		try {
			List<Feed> feeds = dataManagement.getAllFeeds();
			logger.info("Actually received something from feed collection ! ! ");
			return feeds;
		} catch (FeedReaderException ex) {
			return new ArrayList<Feed>();
		}	
	}
	
	@RequestMapping(
			value = "/{feedname}/article", 
			method = RequestMethod.POST, 
			consumes = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody Messages createArticle(@PathVariable String feedname, @RequestBody String pBody) {
		
		logger.info("I am reaching here with feedname: " + feedname);
		try {
			String id_created = dataManagement.createArticle(feedname, pBody);
			return new Messages(200, "New article with id : " + id_created + " created in feed: "+ feedname);
		} catch (FeedReaderException ex) {
			return new Messages(404, "Artcile could not be successfully added ! !. May be the feed itself wasn't present in the Database ");
		}	
	}
	
	@RequestMapping(
			value = "/{feedname}/subscribe/{username}", 
			method = RequestMethod.POST)
	
	public @ResponseBody Messages subsribeUser(@PathVariable String feedname, @PathVariable String username) {
		
		logger.info("I am reaching here with feedname: " + feedname);
		try {
			dataManagement.subscribeFeed(feedname, username);
			return new Messages(200, "User : " + username + " has been subscribed to: "+ feedname);
		} catch (FeedReaderException ex) {
			return new Messages(404, ex.getMessage());
		}	
	}
	
}
