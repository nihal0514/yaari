package com.example.yaari.model.publicpost;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PublicPostResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("posts")
	private List<PublicPostsItem> posts;

	@SerializedName("status")
	private int status;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setPosts(List<PublicPostsItem> posts){
		this.posts = posts;
	}

	public List<PublicPostsItem> getPosts(){
		return posts;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	public PublicPostResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}
}