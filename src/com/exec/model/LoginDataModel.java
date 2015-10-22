package com.exec.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;


@Entity
@Table(name = "USER_LOGIN", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "USER_MOBILE" }),
		@UniqueConstraint(columnNames = { "USER_EMAIL" }) })
public class LoginDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6834447851124297127L;
	private String userMobile;
	private String userEmail;
	private String userPassword;
	private Integer userId;
	private String userOtpHashcode;
	private UserDataModel userDataModel;

	@Id
	@Column(name = "USER_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "USER_PASSWORD", unique = false, nullable = false)
	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	@Column(name = "USER_MOBILE", unique = true, nullable = false, length=10)
	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	@Column(name = "USER_EMAIL", unique = true, nullable = false)
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "userLoginDataModel", cascade = CascadeType.ALL)
	public UserDataModel getUserDataModel() {
		return userDataModel;
	}

	public void setUserDataModel(UserDataModel userDataModel) {
		this.userDataModel = userDataModel;
	}
	@Column(name = "USER_OTP_HASHCODE", nullable = true)
	public String getUserOtpHashcode() {
		return userOtpHashcode;
	}

	public void setUserOtpHashcode(String userOtpHashcode) {
		this.userOtpHashcode = userOtpHashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return this.getUserEmail().equals(
					((LoginDataModel) obj).getUserEmail())
					|| this.getUserMobile().equals(
							((LoginDataModel) obj).getUserMobile());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return userEmail + "  " + userMobile + "  " + userPassword + " "
				+ userId;
	}
}
