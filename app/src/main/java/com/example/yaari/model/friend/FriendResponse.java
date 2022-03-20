package com.example.yaari.model.friend;

public class FriendResponse{

	private Result result;

	private String message;

	private int status;

	public FriendResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
		return result;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}