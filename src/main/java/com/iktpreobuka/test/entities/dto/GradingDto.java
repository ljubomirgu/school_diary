package com.iktpreobuka.test.entities.dto;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.iktpreobuka.test.enumerations.EGradeType;
import com.iktpreobuka.test.enumerations.ESemester;

public class GradingDto {
	
//	@NotNull(message = "Semester  must be provided.")
	private String semester;

//	@NotNull(message = "Grade  must be provided.")
	@Min(value=1, message="Grade must be 1 or higher!")
	@Max(value=5, message="Grade must be 5 or less!")
	private Integer grade;
	
//	@NotNull(message = "Subjects id must be provided.")
	private Integer subjectId;
	
//	@NotNull(message = "Students id  must be provided.")
	private Integer studentId;	
	
//	@NotNull(message = "Teachers id  must be provided.")
	private Integer teacherId;

	public GradingDto() {
		super();
	}

	public GradingDto(String semester, Integer grade, Integer subjectId,
				Integer studentId,Integer teacherId) {
		super();
		this.semester = semester;
		this.grade = grade;
		this.subjectId = subjectId;
		this.studentId = studentId;
		this.teacherId = teacherId;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public Integer getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}

	
	
	

}
