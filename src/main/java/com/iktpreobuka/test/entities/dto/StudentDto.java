package com.iktpreobuka.test.entities.dto;


import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.enumerations.EUserRole;

public class StudentDto{
	
//	@NotNull(message = "First name  must be provided.")
	private String firstName;
	
//	@NotNull(message = "Last name  must be provided.")
	private String lastName;	
	
//	@NotNull(message = "JMBG   must be provided.")
//	@Size(min=13, max=13, message = "JMBG must be {min}  characters long.")
	@Pattern(regexp = "^[0-9]{13}", message = "JMBG must contains only digits between 0 and 9")
	private String jmbg;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//	@NotNull(message = "Date of birth must be provided.")
	@Past(message = "The date of birth must be a date from the past")
	private Date dateOfBirth;
	
	
//	@NotNull(message = "Date of entry must be provided.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Past(message = "Date of entry must be a date from the past")
	private Date dateEntered;

	private String note;
	
	
//	@NotNull(message = "Class id must be provided.")
	private Integer classId;
	
	@Pattern(regexp = "^[a-zA-Z0-9_]{3,20}", message = "Username can contains only digits,letters"
			+ " and underscore.Username must be between 3 and 20 caracters long")
//	@NotNull(message = "Username  must be provided.")
	private String username;

//	@NotNull(message = "Password  must be provided.")
	private String password;

	public StudentDto() {
		super();
	}

	public StudentDto(String firstName, String lastName, String jmbg, Date dateOfBirth,
			 Date dateEntered,String note, String username, String password, Integer classid) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
		this.dateEntered = dateEntered;
		this.note = note;
		this.username = username;
		this.password = password;
		this.classId=classId;
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

	public Date getDateEntered() {
		return dateEntered;
	}

	public void setDateEntered(Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}	
}
