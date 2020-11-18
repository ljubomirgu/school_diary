package com.iktpreobuka.test.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "student")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
//@DiscriminatorValue("student")
@PrimaryKeyJoinColumn(name = "studentId")
public class StudentEntity extends UserEntity {

//	@JsonView(Views.Admin.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "date_of_entry")
	@NotNull(message = "Date of entry must be provided.")
//	@Past(message = "Date of entry must be a date from the past")
	private Date dateEntered;

//	@JsonView(Views.Teacher.class)
	@Column(name = "note")
	private String note;

	
//	@JsonManagedReference(value="class-student")
//	@JsonView(Views.Teacher.class)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "schoolClass")
	@NotNull(message = "Class must be provided.")
	private ClassEntity schoolClass;

	@JsonBackReference(value="parent-student")
//	@JsonIgnore
//	@JsonView(Views.Teacher.class)
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Parent_student", joinColumns =
	{@JoinColumn(name = "Student_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "Parent_id",
	nullable = false, updatable = false) })
	private List<ParentEntity> parents = new ArrayList<>();
	
//	@JsonBackReference(value="grading-student")
//	@JsonIgnore
//	@JsonView(Views.Student.class)
	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<GradingEntity> gradings = new ArrayList<>();

	public StudentEntity() {
		super();
	}

	public StudentEntity(Date dateEntered, String note, ClassEntity schoolClass, List<ParentEntity> parents,
			List<GradingEntity> gradings) {
		super();
		this.dateEntered = dateEntered;
		this.note = note;
		this.schoolClass = schoolClass;
		this.parents = parents;
		this.gradings = gradings;
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

	public ClassEntity getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(ClassEntity schoolClass) {
		this.schoolClass = schoolClass;
	}


	public List<ParentEntity> getParents() {
		return parents;
	}

	public void setParents(List<ParentEntity> parents) {
		this.parents = parents;
	}

	public List<GradingEntity> getGradings() {
		return gradings;
	}

	public void setGradings(List<GradingEntity> gradings) {
		this.gradings = gradings;
	}


}
