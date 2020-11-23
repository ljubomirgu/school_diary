package com.iktpreobuka.test.entities.dto;



import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;


public class UserDto {
	
//	@NotNull(message = "First name  must be provided.")
	private String firstName;
	
//	@NotNull(message = "Last name  must be provided.")
	private String lastName;	
	
//	@NotNull(message = "JMBG   must be provided.")
	@Size(min=13, max=13, message = "JMBG must be {min}  characters long.")
	private String jmbg;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//	@NotNull(message = "Date of birth must be provided UserDto.")
//	@Past(message = "The date of birth must be a date from the past")
	private Date dateOfBirth;
	
	public UserDto() {
		super();
	}
	public UserDto(String firstName, String lastName, String jmbg, Date dateOfBirth) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
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
	
	
	

}
