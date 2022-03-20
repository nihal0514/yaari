package com.example.yaari.model.Profile;
public class ProfileResponse{

	private Profile profile;

	private String message;

	private int status;

	public void setProfile(Profile profile){
		this.profile = profile;
	}

	public Profile getProfile(){
		return profile;
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

	public ProfileResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}