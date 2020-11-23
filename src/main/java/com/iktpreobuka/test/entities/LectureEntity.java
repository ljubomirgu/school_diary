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
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "lecture")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LectureEntity {

//	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "lecture_id")
	private Integer lectureId;

//	@JsonManagedReference(value="lecture-teacher")
//	@JsonView(Views.Student.class)
	@NotNull
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher")
	private TeacherEntity teacher;
	
//	@JsonManagedReference(value="lecture-subject")
//	@JsonView(Views.Student.class)
	@NotNull
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "subject")
	private SubjectEntity subject;
	
//	@JsonManagedReference(value="class-lecture")
//	@JsonView(Views.Teacher.class)
	@NotNull
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "schoolClass")
	private ClassEntity schoolClass;
	
//	@JsonBackReference(value="lecture-grading")
//	@JsonView(Views.Student.class)
	@JsonIgnore
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<GradingEntity> gradings = new ArrayList<>();

	@JsonView(Views.Admin.class)
	@Version
	private Integer version;
	public LectureEntity() {
		super();
	}


	public LectureEntity(Integer lectureId, TeacherEntity teacher, SubjectEntity subject, ClassEntity schoolClass,
			List<GradingEntity> gradings, Integer version) {
		super();
		this.lectureId = lectureId;
		this.teacher = teacher;
		this.subject = subject;
		this.schoolClass = schoolClass;
		this.gradings = gradings;
		this.version = version;
	}


	public Integer getLectureId() {
		return lectureId;
	}

	public void setLectureId(Integer lectureId) {
		this.lectureId = lectureId;
	}

	public TeacherEntity getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherEntity teacher) {
		this.teacher = teacher;
	}

	public SubjectEntity getSubject() {
		return subject;
	}

	public void setSubject(SubjectEntity subject) {
		this.subject = subject;
	}

	public ClassEntity getSchoolClass() {
		return schoolClass;
	}

	public void setSchoolClass(ClassEntity schoolClass) {
		this.schoolClass = schoolClass;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public List<GradingEntity> getGradings() {
		return gradings;
	}


	public void setGradings(List<GradingEntity> gradings) {
		this.gradings = gradings;
	}

}
