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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "subject")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class SubjectEntity {

//	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "subject_id")
	private Integer subjectId;

//	@JsonView(Views.Student.class)
	@Column(name = "subject_name", unique = true)
	@NotNull(message = "Subject name  must be provided.")
	private String subjectName;

//	@JsonView(Views.Teacher.class)
	@Column(name = "weekly_fund")
	@NotNull(message = "Weekly fund  must be provided.")
	private Integer weeklyFund;

//	@JsonBackReference (value="lecture-subject")
//	@JsonView(Views.Teacher.class)
	@JsonIgnore
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<LectureEntity> lectures = new ArrayList<>();
	
//	@JsonView(Views.Admin.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "year_subject", joinColumns =
	{@JoinColumn(name = "subject_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "year_id",
	nullable = false, updatable = false) })
	private List<YearEntity> years = new ArrayList<>();
	
//	@JsonView(Views.Admin.class)
	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "teacher_subject", joinColumns =
	{@JoinColumn(name = "subject_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "teacher_id",
	nullable = false, updatable = false) })
	private List<TeacherEntity> teachers = new ArrayList<>();

//	@JsonView(Views.Admin.class)
	@Version
	private Integer version;

	public SubjectEntity() {
		super();
	}

	public SubjectEntity(Integer subjectId, String subjectName, Integer weeklyFund, List<LectureEntity> lectures,
			Integer version) {
		super();
		this.subjectId = subjectId;
		this.subjectName = subjectName;
		this.weeklyFund = weeklyFund;
		this.lectures = lectures;
		this.version = version;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getWeeklyFund() {
		return weeklyFund;
	}

	public void setWeeklyFund(Integer weeklyFund) {
		this.weeklyFund = weeklyFund;
	}

	public List<LectureEntity> getLectures() {
		return lectures;
	}

	public void setLectures(List<LectureEntity> lectures) {
		this.lectures = lectures;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	public List<YearEntity> getYears() {
		return years;
	}

	public void setYears(List<YearEntity> years) {
		this.years = years;
	}

	public List<TeacherEntity> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<TeacherEntity> teachers) {
		this.teachers = teachers;
	}

}
