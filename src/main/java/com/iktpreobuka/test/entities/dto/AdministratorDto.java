package com.iktpreobuka.test.entities.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iktpreobuka.test.enumerations.EUserRole;

public class AdministratorDto{

//	@NotNull(message = "First name  must be provided.")
	private String firstName;
	
//	@NotNull(message = "Last name  must be provided.")
	private String lastName;	
	
//	@NotNull(message = "JMBG   must be provided.")
	@Size(min=13, max=13, message = "JMBG must be {min}  characters long.")
	private String jmbg;


	private Date dateOfBirth;
	
	@Size(min=9, max=12, message = "Phone number must be between {min} and {max} characters long.")
//	@NotNull(message = "Phone number must be provided")
	private String phoneNumber;
	
//može i neko ograničenje za dužinu i sadržaj karaktera za username i pass:
//	@NotNull(message = "Username  must be provided.")
	private String username;

//	@NotNull(message = "Password  must be provided.")
	private String password;

	public AdministratorDto() {
		super();
	}
/*
	public AdministratorDto(String firstName, String lastName, String jmbg, EUserRole role, Date dateOfBirth,
			String phoneNumber) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.role = role;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
	}
	
	*/

	public AdministratorDto( String firstName, String lastName, String jmbg,
			Date dateOfBirth, String phoneNumber, String username, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.username = username;
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}


	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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


	
	
	
}
