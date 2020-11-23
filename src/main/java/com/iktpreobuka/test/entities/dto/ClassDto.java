package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.NotNull;


public class ClassDto {
	
//  @NotNull(message = "Number of department must be provided.")
	private String numberOfDepartment;
	
//	@NotNull(message = "Year must be provided.")
	private Integer yearId;
	
/*
	@NotNull(message = "Year must be provided.")

	private String year;
*/	
	
//	@NotNull(message = "School year must be provided.")
	private String schoolYear;
	
	public ClassDto() {
		super();
	}
	public ClassDto(String numberOfDepartment, Integer yearId, String schoolYear) {
		super();
		this.numberOfDepartment = numberOfDepartment;
		this.yearId = yearId;
		this.schoolYear=schoolYear;
	}

	public String getNumberOfDepartment() {
		return numberOfDepartment;
	}


	public void setNumberOfDepartment(String numberOfDepartment) {
		this.numberOfDepartment = numberOfDepartment;
	}

	public Integer getYearId() {
		return yearId;
	}

	public void setYearId(Integer yearId) {
		this.yearId = yearId;
	}
	public String getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}


}
