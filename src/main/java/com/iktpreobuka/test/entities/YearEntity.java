package com.iktpreobuka.test.entities;

import java.util.ArrayList;
import java.util.List;

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
import com.iktpreobuka.test.enumerations.EYear;
import com.iktpreobuka.test.security.Views;

@Entity
@Table(name = "year")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class YearEntity {

//	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "year_id")
	private Integer yearId;
	
//	@JsonView(Views.Teacher.class)
    @Enumerated(EnumType.STRING)
	@Column(name = "year", unique = true)
	@NotNull(message = "Year must be provided.")
	private EYear year;


//	@JsonBackReference(value="class-year")
//	@JsonView(Views.Parent.class)
	@JsonIgnore
	@OneToMany(mappedBy = "year", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<ClassEntity> classes = new ArrayList<>();
	
//	@JsonView(Views.Parent.class)
//	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinTable(name = "year_subject", joinColumns =
	{@JoinColumn(name = "year_id", nullable = false, updatable = false) },
	inverseJoinColumns = { @JoinColumn(name = "subject_id",
	nullable = false, updatable = false) })
	private List<SubjectEntity> subjects = new ArrayList<>();

//	@JsonView(Views.Admin.class)
	@Version
	private Integer version;

	public YearEntity() {
		super();
	}

	public YearEntity(Integer yearId, EYear year, List<ClassEntity> classes, Integer version) {
		super();
		this.yearId = yearId;
		this.year = year;
		this.classes = classes;
		this.version = version;
	}

	public Integer getYearId() {
		return yearId;
	}

	public void setYearId(Integer yearId) {
		this.yearId = yearId;
	}

	public EYear getYear() {
		return year;
	}

	public void setYear(EYear year) {
		this.year = year;
	}

	public List<ClassEntity> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassEntity> classes) {
		this.classes = classes;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public List<SubjectEntity> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<SubjectEntity> subjects) {
		this.subjects = subjects;
	}

}
