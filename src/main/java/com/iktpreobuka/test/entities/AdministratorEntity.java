package com.iktpreobuka.test.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "administrator")//, uniqueConstraints = {@UniqueConstraint(columnNames = "jmbg")})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
//@DiscriminatorValue("4")
@PrimaryKeyJoinColumn(name = "adminId")
public class AdministratorEntity extends UserEntity {
	
	@JsonView(Views.Admin.class)
	@NotNull(message = "Phone number must be provided")
	@Column(name = "phone_number")
	@Size(min=9, max=12, message = "Phone number must be between {min} and {max} characters long.")
	private String phoneNumber;

	public AdministratorEntity() {
		super();
	}

	public AdministratorEntity(String phoneNumber) {
		super();
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	

}
