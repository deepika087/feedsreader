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
		
		logger.info("I am reaching here with feedname: " + feedname);
		try {
			String id_created = dataManagement.createFeed(feedname);
			return new Messages(200, "Feed is created with unique id: " + id_created);
		} catch (FeedReaderException ex) {
			return new Messages(404, "Feed with this name is already present");
		}	
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Feed> getFeeds() {
		
		logger.info("I am reaching here to fetch all feeds: " );
		try {
			List<Feed> feeds = dataManagement.getAllFeeds();
			logger.info("Actually received something ! ! ");
			return feeds;
		} catch (FeedReaderException ex) {
			return new ArrayList<Feed>();
		}	
	}

}
