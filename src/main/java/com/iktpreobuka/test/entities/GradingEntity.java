package com.iktpreobuka.test.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.enumerations.EGradeType;
import com.iktpreobuka.test.enumerations.ESemester;
import com.iktpreobuka.test.security.Views;


@Entity
@Table(name = "grading")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class GradingEntity {
	
//	@JsonView(Views.Teacher.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "grading_id")
	private Integer gradingId;
	
//	@JsonView(Views.Student.class)
    @Enumerated(EnumType.STRING)
	@Column(name = "semester", length=50)
	@NotNull(message = "Semester  must be provided.")
	private ESemester semester;
	
	
//	@JsonView(Views.Student.class)
	@Column(name = "grade")
	@NotNull(message = "Grade  must be provided.")
	@Min(value=1, message="Grade must be 1 or higher!")
	@Max(value=5, message="Grade must be 5 or less!")
	private Integer grade;

//	@JsonManagedReference(value="grading-student")
//	@JsonView(Views.Teacher.class)
	@JsonIgnore()
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "student")
	private StudentEntity student;
	
//	@JsonManagedReference(value="lecture-grading")
//	@JsonView(Views.Student.class)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture")
	private LectureEntity lecture;
	
//	@JsonView(Views.Admin.class)
	@Version
	private Integer version;

	public GradingEntity() {
		super();
	}
	
	public GradingEntity(Integer gradingId, ESemester semester,
			Integer grade,StudentEntity student, LectureEntity lecture, Integer version) {
		super();
		this.gradingId = gradingId;
		this.semester = semester;
		this.grade = grade;
		this.student = student;
		this.lecture = lecture;
		this.version = version;
	}
	
	
	public GradingEntity(ESemester semester,Integer grade,
			StudentEntity student, LectureEntity lecture) {
		super();
		this.semester = semester;
		this.grade = grade;
		this.student = student;
		this.lecture = lecture;
	}

	public Integer getGradingId() {
		return gradingId;
	}

	public void setGradingId(Integer gradingId) {
		this.gradingId = gradingId;
	}

	public ESemester getSemester() {
		return semester;
	}

	public void setSemester(ESemester semester) {
		this.semester = semester;
	}
	
	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public StudentEntity getStudent() {
		return student;
	}

	public void setStudent(StudentEntity student) {
		this.student = student;
	}

	public LectureEntity getLecture() {
		return lecture;
	}

	public void setLecture(LectureEntity lecture) {
		this.lecture = lecture;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	

}
