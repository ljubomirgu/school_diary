package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.NotNull;

import com.iktpreobuka.test.enumerations.EYear;

public class YearDto {	
	
//	@NotNull(message = "Year must be provided.")
	private String year;

	public YearDto() {
		super();
	}

	public YearDto(@NotNull(message = "Year must be provided.") String year) {
		super();
		this.year = year;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
}
