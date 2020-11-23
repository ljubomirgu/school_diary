package com.iktpreobuka.test.entities.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ParentDto {

//	@NotNull(message = "First name  must be provided.")
	private String firstName;

//	@NotNull(message = "Last name  must be provided.")
	private String lastName;

//	@NotNull(message = "JMBG   must be provided.")
//	@Size(min = 13, max = 13, message = "JMBG must be {min}  characters long.")
	@Pattern(regexp = "^[0-9]{13}", message = "JMBG must contains only digits between 0 and 9")
	private String jmbg;
	
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//	@NotNull(message = "Date of birth must be provided.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Past(message = "The date of birth must be a date from the past")
	private Date dateOfBirth;

//	@NotNull(message = "Email  must be provided.")
	@Email
	private String email;

//	@NotNull(message="Parent must have children as students.")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<Integer> studentsId;
	
//	@NotNull(message = "Username  must be provided.")
	private String username;

//	@NotNull(message = "Password  must be provided.")
	private String password;
	
	public ParentDto() {
		super();
	}

	public ParentDto(String firstName, String lastName, String jmbg, Date dateOfBirth, String email,
			List<Integer> studentsId, String username, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
		this.email = email;
		this.studentsId = studentsId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
/*
	public List<String> getStudentsJmbg() {
		return studentsJmbg;
	}

	public void setStudentJmbg(List<String> studentsJmbg) {
		this.studentsJmbg = studentsJmbg;
	}
*/
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

	public List<Integer> getStudentsId() {
		return studentsId;
	}
	public void setStudentsId(List<Integer> studentsId) {
		this.studentsId = studentsId;
	}

}
