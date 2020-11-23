package com.iktpreobuka.test.entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "teacher")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
//@DiscriminatorValue("teacher")
@PrimaryKeyJoinColumn(name = "teacherId")
public class TeacherEntity extends UserEntity {

//	@JsonView(Views.Teacher.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "date_of_employment")
	@NotNull(message = "Date of employment  must be provided.")
	private Date dateOfEmployment;

//	@JsonView(Views.Teacher.class)
	@Column(name = "vocation")
	@NotNull(message = "Vocation  must be provided.")
	private String vocation;

//	@JsonBackReference(value="lecture-teacher")
//	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<LectureEntity> lectures = new ArrayList<>();

//	@JsonIgnore
//	@JsonView(Views.Teacher.class)
	@NotNull(message = "Vocation  must be provided.")
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "teacher_subject", joinColumns =
	{@JoinColumn(name = "teacher_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "subject_id",
	nullable = false, updatable = false) })
	private List<SubjectEntity> subjects = new ArrayList<>();
	

	public TeacherEntity() {
		super();
	}


	public TeacherEntity(Date dateOfEmployment, String vocation, List<LectureEntity> lectures, List<SubjectEntity> subjects) {
		super();
		this.dateOfEmployment = dateOfEmployment;
		this.vocation = vocation;
		this.lectures = lectures;
		this.subjects = subjects;
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

	public List<LectureEntity> getLectures() {
		return lectures;
	}

	public void setLectures(List<LectureEntity> lectures) {
		this.lectures = lectures;
	}


	public List<SubjectEntity> getSubjects() {
		return subjects;
	}


	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}

}
