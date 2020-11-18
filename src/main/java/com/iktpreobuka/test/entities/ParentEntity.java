package com.iktpreobuka.test.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;


@Entity
@Table(name = "parent")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
//@DiscriminatorValue("parent")
@PrimaryKeyJoinColumn(name = "parentId")
public class ParentEntity extends UserEntity {
	
//	@JsonView(Views.Teacher.class)
	@Column(name = "parent_email")
	@Email
	private String email;
	
	@JsonManagedReference(value="parent-student")
//	@JsonIgnore
//	@JsonView(Views.Parent.class)  //?????????????????????????
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "Parent_student", joinColumns =
	{@JoinColumn(name = "Parent_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "Student_id",
	nullable = false, updatable = false) })
	private List<StudentEntity> students = new ArrayList<>();

	public ParentEntity() {
		super();
	}
	
	public ParentEntity(String email, List<StudentEntity> students) {
		super();
		this.email = email;
		this.students = students;
	}


	public ParentEntity(String email) {
		super();
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<StudentEntity> students) {
		this.students = students;
	}
	
	

}
