package com.iktpreobuka.test.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.security.Views;

//@MappedSuperclass
@Entity
@Table(name = "user")
// @JsonPropertyOrder({"userId","firstName","lastName","jmbg","role"})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name="user_type", discriminatorType =
// DiscriminatorType.STRING)
// @DiscriminatorValue("admin")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {

	@JsonView(Views.Admin.class)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer userId;

	@JsonView(Views.Parent.class)
	@Column(name = "first_name")
	@NotNull(message = "First name  must be provided.")
	private String firstName;

	@JsonView(Views.Parent.class)
	@Column(name = "last_name")
	@NotNull(message = "Last name  must be provided.")
	private String lastName;

	@JsonView(Views.Admin.class)
	@Column(name = "jmbg", unique = true)
	@NotNull(message = "JMBG   must be provided.")
	@Pattern(regexp = "^[0-9]{13}", message = "JMBG must contains only digits between 0 and 9")
//	@Size(min = 13, max = 13, message = "JMBG must be {min}  characters long.")
	private String jmbg;

	@JsonView(Views.Admin.class)
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "date_of_birth")
	@NotNull(message = "Date of birth must be provided.")
//	@Past(message = "The date of birth must be a date from the past")
	private Date dateOfBirth;

	@JsonView(Views.Admin.class)
//	@JsonBackReference(value="account-user")
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	private List<AccountEntity> accounts = new ArrayList<>();

	@JsonView(Views.Admin.class)
	@Version
	private Integer version;

	public UserEntity() {
		super();
	}

	public UserEntity(Integer userId, String firstName, String lastName, String jmbg, Date dateOfBirth,
			List<AccountEntity> accounts, Integer version) {
		super();
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.jmbg = jmbg;
		this.dateOfBirth = dateOfBirth;
		this.accounts = accounts;
		this.version = version;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJmbg() {
		return jmbg;
	}

	public void setJmbg(String jmbg) {
		this.jmbg = jmbg;
	}

	@JsonIgnore
	public List<AccountEntity> getAccount() {
		return accounts;
	}

	public void setAccount(List<AccountEntity> accounts) {
		this.accounts = accounts;
	}

	@JsonIgnore
	public List<AccountEntity> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<AccountEntity> accounts) {
		this.accounts = accounts;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

}
