package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.NotNull;

public class SubjectDto {

//	@NotNull(message = "Subject name  must be provided.")
	private String subjectName;

//	@NotNull(message = "Weekly fund  must be provided.")
	private Integer weeklyFund;

	public SubjectDto() {
		super();
	}

	public SubjectDto(String subjectName,Integer weeklyFund) {
		super();
		this.subjectName = subjectName;
		this.weeklyFund = weeklyFund;
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
	
	
	
	

}
