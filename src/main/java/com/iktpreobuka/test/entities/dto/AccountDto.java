package com.iktpreobuka.test.entities.dto;

import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.enumerations.EUserRole;


public class AccountDto {

//  @NotNull(message = "Username  must be provided.")
	private String username;
    
//	@NotNull(message = "Password  must be provided.")
	private String password;
	
//	@Enumerated(EnumType.STRING)
//	@NotNull(message = "Role  must be provided.")
	private String role;
	
//	@NotNull(message = "User id  must be provided.")
	private Integer userId;
	
	private Boolean isActive;

	public AccountDto() {
		super();
	}

	public AccountDto(String username, String password, String role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
	}  
	
	public AccountDto(String username, String password, String role, Integer userId) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
		this.userId = userId;
	}
	
	

	public AccountDto(String username, String password, Boolean isActive) {
		super();
		this.username = username;
		this.password = password;
		this.isActive = isActive;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


}
