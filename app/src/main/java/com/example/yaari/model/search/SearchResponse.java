package com.example.yaari.model.search;

import java.util.List;

public class SearchResponse{

	private String message;
	private List<User> user;

	private int status;

	public SearchResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setUser(List<User> user){
		this.user = user;
	}

	public List<User> getUser(){
		return user;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}