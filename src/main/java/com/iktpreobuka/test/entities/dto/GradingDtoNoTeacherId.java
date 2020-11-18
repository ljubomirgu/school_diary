package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class GradingDtoNoTeacherId {

	
	private String semester;

	@Min(value=1, message="Grade must be 1 or higher!")
	@Max(value=5, message="Grade must be 5 or less!")
	private Integer grade;
	
	private Integer subjectId;
	
	private Integer studentId;

	public GradingDtoNoTeacherId() {
		super();
	}

	public GradingDtoNoTeacherId(String semester, Integer grade,
			Integer subjectId, Integer studentId) {
		super();
		this.semester = semester;
		this.grade = grade;
		this.subjectId = subjectId;
		this.studentId = studentId;
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
	


}
