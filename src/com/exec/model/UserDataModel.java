package com.exec.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "USER_DATA")
public class UserDataModel {
	private Integer userId;
	private String userName;
	private String userAge;
	private String userGender;
	private String userAddress;
	private String userCity;
	private String userState;
	private String userCountry;
	private String userStatus;

	private LoginDataModel userLoginDataModel;

	
	@Id
	@Column(name = "USER_ID", unique = true, nullable = false)
	@GeneratedValue(generator = "generator")
	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "userLoginDataModel"))
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "USER_NAME", unique = false, nullable = false)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "USER_AGE", unique = false, nullable = true)
	public String getUserAge() {
		return userAge;
	}

	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}

	@Column(name = "USER_GENDER", unique = false, nullable = true)
	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	@Column(name = "USER_ADDRESS", unique = false, nullable = true)
	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	@Column(name = "USER_CITY", unique = false, nullable = true)
	public String getUserCity() {
		return userCity;
	}

	public void setUserCity(String userCity) {
		this.userCity = userCity;
	}

	@Column(name = "USER_STATE", unique = false, nullable = true)
	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	@Column(name = "USER_COUNTRY", unique = false, nullable = true)
	public String getUserCountry() {
		return userCountry;
	}

	public void setUserCountry(String userCountry) {
		this.userCountry = userCountry;
	}

	@Column(name = "USER_STATUS", unique = false, nullable = true)
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	public LoginDataModel getUserLoginDataModel() {
		return userLoginDataModel;
	}

	public void setUserLoginDataModel(LoginDataModel userLoginDataModel) {
		this.userLoginDataModel = userLoginDataModel;
	}

	@Override
	public String toString() {
		return userAddress + userCity + userCountry + userGender + userName + userState + userStatus + userId
				+ userAge;
	}
}
