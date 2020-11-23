package com.iktpreobuka.test.entities.dto;

import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;


public class TeacherDto {

//	@NotNull(message = "First name  must be provided.")
	private String firstName;
	
//	@NotNull(message = "Last name  must be provided.")
	private String lastName;	
	
//	@NotNull(message = "JMBG   must be provided.")
	@Size(min=13, max=13, message = "JMBG must be {min}  characters long.")
	private String jmbg;
		
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//	@NotNull(message = "Date of birth must be provided.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfBirth;
	
	//@Past(message = "Date of employment must be a date from the past")
//	@NotNull(message = "Date of employment  must be provided.")
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfEmployment;

//	@NotNull(message = "Vocation  must be provided.")
	private String vocation;
	
//	@NotNull(message="Subjects must be provided")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<Integer> subjectsId;
//	private List<String> subjects;
	
//	@NotNull(message = "Username  must be provided.")
	private String username;

//	@NotNull(message = "Password  must be provided.")
	private String password;

	public TeacherDto() {
		super();
	}

	public TeacherDto(String firstName, String lastName, String jmbg, Date dateOfBirth,
			Date dateOfEmployment, String vocation, List<Integer> subjectsId, String username, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
		this.dateOfEmployment = dateOfEmployment;
		this.vocation = vocation;
		this.subjectsId = subjectsId;
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

	public Date getDateOfEmployment() {
		return dateOfEmployment;
	}

	public void setDateOfEmployment(Date dateOfEmployment) {
		this.dateOfEmployment = dateOfEmployment;
	}

	public String getVocation() {
		return vocation;
	}

	public void setVocation(String vocation) {
		this.vocation = vocation;
	}

	public List<Integer> getSubjectsId() {
		return subjectsId;
	}

	public void setSubjectsId(List<Integer> subjectsId) {
		this.subjectsId = subjectsId;
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
