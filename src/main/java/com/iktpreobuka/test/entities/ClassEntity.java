package com.iktpreobuka.test.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "class", uniqueConstraints = { @UniqueConstraint(columnNames={ "year", "school_year", "number_of_department" })})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ClassEntity {
	
//	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "class_id")
	private Integer classId;

//	@JsonManagedReference(value = "class-year")
//	@JsonView(Views.Teacher.class)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "year")
	@NotNull(message = "Year must be provided.")
	private YearEntity year;
	
//	@JsonBackReference(value="class-lecture")
//	@JsonView(Views.Admin.class)
	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<LectureEntity> lectures = new ArrayList<>();

//	@JsonBackReference(value="class-student")
//	@JsonView(Views.Admin.class)
	@JsonIgnore
	@OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<StudentEntity> students = new ArrayList<>();

//	@JsonView(Views.Parent.class)
	@Column(name = "number_of_department")
	@NotNull(message = "Number of department must be provided.")
	private String numberOfDepartment;
	
//	@JsonView(Views.Parent.class)
	@Column(name = "school_year")
	@NotNull(message = "School year must be provided.")
	private String schoolYear;
	
	@Version
	private Integer version;	
	
	public ClassEntity() {
		super();
	}
	
	public ClassEntity(Integer classId, YearEntity year, List<LectureEntity> lectures, List<StudentEntity> students,
		   String numberOfDepartment, Integer version, String schoolYear) {
		super();
		this.classId = classId;
		this.year = year;
		this.lectures = lectures;
		this.students = students;
		this.numberOfDepartment = numberOfDepartment;
		this.version = version;
		this.schoolYear=schoolYear;
	}
	

	public Integer getClassId() {
		return classId;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public YearEntity getYear() {
		return year;
	}

	public void setYear(YearEntity year) {
		this.year = year;
	}

	public List<LectureEntity> getLectures() {
		return lectures;
	}

	public void setLectures(List<LectureEntity> lectures) {
		this.lectures = lectures;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getNumberOfDepartment() {
		return numberOfDepartment;
	}

	public void setNumberOfDepartment(String numberOfDepartment) {
		this.numberOfDepartment = numberOfDepartment;
	}

	public String getSchoolYear() {
		return schoolYear;
	}

	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
	
	
	
	

}
