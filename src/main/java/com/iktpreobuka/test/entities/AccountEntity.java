package com.iktpreobuka.test.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.security.Views;


@Entity
@Table(name = "account")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AccountEntity {
	
//	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private Integer accountId;

//	@JsonView(Views.Admin.class)
	@NotNull(message = "Username  must be provided.")
	@Column(name = "username", unique=true)
	private String username;

//	@JsonIgnore
//	@JsonView(Views.Admin.class)
	@NotNull(message = "Password  must be provided.")
	@Column(name = "password")
	private String password;
	

//	@JsonView(Views.Admin.class)
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Role  must be provided.")
	@JoinColumn(name = "role")
	private EUserRole role;


//	@JsonManagedReference(value="account-user")
//	@JsonView(Views.Admin.class)
//	@JsonIgnore //da ne bi ulazio u rekurziju kod getAllUsers!!!
	@NotNull(message = "User  must be provided.")
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserEntity user;
	
//	@JsonView(Views.Admin.class)
	@NotNull(message = "Status  must be provided.")	
	@JoinColumn(name = "user_status")
	private Boolean isActive;
	
	@Version
	private Integer version;

	public AccountEntity() {
		super();
	}

	public AccountEntity( String username,String password,EUserRole role) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
		this.isActive = true;
	}

	public AccountEntity(Integer accountId, String username,String password,EUserRole role,
			UserEntity user, Integer version) {
		super();
		this.accountId = accountId;
		this.username = username;
		this.password = password;
		this.role = role;
		this.user = user;
		this.isActive = true;
		this.version = version;
	}


	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
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

	public EUserRole getRole() {
		return role;
	}

	public void setRole(EUserRole role) {
		this.role = role;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
