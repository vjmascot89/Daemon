package com.exec.model;

public class PublishableUserDataModel {
	private Integer userId;
	private String userName;
	private String userAge;
	private String userGender;
	private String userAddress;
	private String userCity;
	private String userState;
	private String userCountry;
	private String userStatus;

	public PublishableUserDataModel() {
	}

	public PublishableUserDataModel(UserDataModel userModel) {
		userId = userModel.getUserId();
		userName = userModel.getUserName();
		userAge = userModel.getUserAge();
		userGender = userModel.getUserGender();
		userAddress = userModel.getUserAddress();
		userCity = userModel.getUserCity();
		userState = userModel.getUserState();
		userCountry = userModel.getUserCountry();
		userStatus = userModel.getUserStatus();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAge() {
		return userAge;
	}

	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getUserCity() {
		return userCity;
	}

	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	@Override
	public String toString() {
		return "UserDataModel [userId=" + userId + ", userName=" + userName
				+ ", " + "userAge=" + userAge + ", userGender=" + userGender
				+ ", " + "userAddress=" + userAddress + ", userCity="
				+ userCity + ", " + "userState=" + userState + ", userCountry="
				+ userCountry + ", " + "userStatus=" + userStatus + "]";
	}
}
