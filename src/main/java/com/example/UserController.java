package com.example;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.datamodels.FeedData;
import com.example.datamodels.Messages;
import com.example.dataservice.DataManagement;
import com.example.exceptions.FeedReaderException;

@Controller
@RequestMapping("/user/{username}")
public class UserController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DataManagement dataManagement;
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Messages add(@PathVariable String username) {
		
		logger.info("I am reaching here with user name: " + username);
		try {
			String id_created = dataManagement.createUser(username);
			return new Messages(200, "User created with id: " + id_created);
		} catch (FeedReaderException ex) {
			return new Messages(404, "Username already present");
		}	
	}
	
	@RequestMapping(value = "/feeds", method = RequestMethod.GET) 
	public @ResponseBody List<FeedData> getFeeds(@PathVariable String username) {
		
		try {
			logger.info("Request to return Feeds for user: " + username);
			List<FeedData> feeds = dataManagement.getFeedData(username);
			logger.info("Feeds received: "+ feeds);
			return feeds;
		} catch (FeedReaderException ex) {
			return new ArrayList<FeedData>();
		}	
		
	}
	
}
