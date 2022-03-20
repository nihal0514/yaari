package com.example.yaari.model;


public class GeneralResponse{

	private String message;

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	private String extra;

	private int status;

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

	public GeneralResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}
}