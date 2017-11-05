package com.example.datamodels;

import java.util.List;

import org.springframework.data.annotation.Id;

public class Feed {
	
	@Id
	private String id;
	
	private String feedname;
	
	private List<String> articleIds;

	public String getFeedname() {
		return feedname;
	}

	public void setFeedname(String feedname) {
		this.feedname = feedname;
	}

	public List<String> getArticleIds() {
		return articleIds;
	}

	public void setArticleIds(List<String> articleIds) {
		this.articleIds = articleIds;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Feed(String feedname, List<String> articleIds) {
		this.feedname = feedname;
		this.articleIds = articleIds;
	}
}
