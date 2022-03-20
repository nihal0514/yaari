package com.example.yaari.model.friend;

import java.util.List;

public class Result{

	private List<Request> requests;

	private List<Friend> friends;

	public void setRequests(List<Request> requests){
		this.requests = requests;
	}

	public List<Request> getRequests(){
		return requests;
	}

	public void setFriends(List<Friend> friends){
		this.friends = friends;
	}

	public List<Friend> getFriends(){
		return friends;
	}
}