package com.example.datamodels;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.bson.types.ObjectId;

public class User {
	
	@Id
	private String id;
	
	private String username;
	
	private List<String> feedIds;

	
	public User(String username, List<String> feedIds) {
		this.username = username;
		this.feedIds = feedIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getFeedIds() {
		return feedIds;
	}

	public void setFeedIds(List<String> feedIds) {
		this.feedIds = feedIds;
	}
	
	
	

}
