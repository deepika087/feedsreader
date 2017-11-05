package com.example.datamodels;

import org.springframework.data.annotation.Id;

public class Article {
	
	@Id
	private String id;
	private String content;
	
	public String getContent() {
		return content;
	}
	
	public Article(String content) {
		this.content = content;
	}
	
}
