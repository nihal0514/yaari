package com.example.yaari.model.post;

import java.util.List;

public class PostResponse{

	private String message;

	private List<PostsItem> posts;

	private int status;

	public PostResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setPosts(List<PostsItem> posts){
		this.posts = posts;
	}

	public List<PostsItem> getPosts(){
		return posts;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}