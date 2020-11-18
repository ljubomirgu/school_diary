package com.iktpreobuka.test.entities.dto;

import javax.validation.constraints.NotNull;

public class LectureDto {
	
//	@NotNull
	private Integer teacherId;
//	@NotNull
	private Integer subjectId;
//	@NotNull
	private Integer classId;
	
	public LectureDto() {
		super();
	}
	public LectureDto(Integer tecaherId, Integer subjectId, Integer classId) {
		super();
		this.teacherId = tecaherId;
		this.subjectId = subjectId;
		this.classId = classId;
	}
	public Integer getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(Integer tecaherId) {
		this.teacherId = tecaherId;
	}
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getClassId() {
		return classId;
	}
	public void setClassId(Integer classId) {
		this.classId = classId;
	}
}
