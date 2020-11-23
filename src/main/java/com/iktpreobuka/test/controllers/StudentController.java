package com.iktpreobuka.test.controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.controllers.util.RESTError;
import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.StudentDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.repositories.ClassRepository;
import com.iktpreobuka.test.repositories.GradingRepository;
import com.iktpreobuka.test.repositories.ParentRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.AccountDao;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/students")
public class StudentController {
	
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private GradingRepository gradingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ParentRepository parentRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET) //, value="/admin")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllStudents() {
		logger.info("StudentController - getAllStudents - starts.");
		return new ResponseEntity<Iterable<StudentEntity>>(studentRepository.findAll(), HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN" ,"ROLE_STUDENT"})
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findStudentById(@PathVariable Integer id) {
		logger.info("StudentController - findStudentById - starts.");
		try {
			StudentEntity student = studentRepository.findById(id).orElse(null);

			if (student == null) {
				logger.info("StudentController - findStudentById - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("StudentController - findStudentById - finished.");
				return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("StudentController - findStudentById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@SuppressWarnings("null")
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewStudent(@Valid @RequestBody StudentDto newStudent, BindingResult result) {
		logger.info("StudentController - addNewStudent - starts.");
		try {
			if(result.hasErrors()) {
				logger.info("StudentController - addNewStudent - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
	
			if (newStudent == null) {
				logger.info("StudentController - addNewStudent - student object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Student object is invalid."),
						HttpStatus.BAD_REQUEST);
			}		
	
			if (newStudent.getFirstName() == null || newStudent.getLastName() == null || newStudent.getDateOfBirth() == null
					|| newStudent.getJmbg() == null || newStudent.getDateOfBirth() == null ||	newStudent.getDateEntered() == null
					|| newStudent.getClassId() == null || newStudent.getUsername()==null || newStudent.getPassword()==null) {
				logger.info("StudentController - addNewStudent - student object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Student object is invalid."),
						HttpStatus.BAD_REQUEST);
			}	
		
			ClassEntity schoolClass = classRepository.findById(newStudent.getClassId()).orElse(null);
			if(schoolClass == null) {
				logger.info("StudentController - addNewStudent - class not found, student not created.");
				return new ResponseEntity<RESTError>(new RESTError("Class not found, student not created."),HttpStatus.NOT_FOUND);
			}	
		
			UserEntity user = userRepository.findByJmbg(newStudent.getJmbg());
			if (user != null) {

				StudentEntity studentExists = studentRepository.findById(user.getUserId()).orElse(null);

				if (studentExists != null) {
					logger.info("StudentController - addNewStudent - there is student with that jmbg.");
					return new ResponseEntity<RESTError>(new RESTError("Student with that jmbg already exists."),
							HttpStatus.BAD_REQUEST);
				}
				logger.info("StudentController - addNewStudent - there is user with same jmbg .");
				return new ResponseEntity<RESTError>(new RESTError("There is another person with same jmbg which can't be student and some other type of user at same time."),
						HttpStatus.BAD_REQUEST);
	}
	
			StudentEntity student = new StudentEntity();
			student.setFirstName(newStudent.getFirstName());
			student.setLastName(newStudent.getLastName());
			student.setJmbg(newStudent.getJmbg());
			student.setDateEntered(newStudent.getDateEntered());
			student.setDateOfBirth(newStudent.getDateOfBirth());		
			student.setSchoolClass(schoolClass);
			student.setNote(newStudent.getNote());
			studentRepository.save(student);
		
			UserEntity userS = (UserEntity)student;
			try {	
				accountDao.createAndSaveAccount(newStudent.getUsername(), newStudent.getPassword(), userS, EUserRole.ROLE_STUDENT);
			}
			catch (Exception e) {
				studentRepository.deleteById(user.getUserId());
				logger.info("StudentController - addNewStudent - account and teacher not created.");
				return new ResponseEntity<RESTError>(new RESTError("Account not created and student not recorded."),HttpStatus.INTERNAL_SERVER_ERROR);

			}
		
			logger.info("StudentController - addNewStudent - finished.");
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);	
		}
		catch (Exception e){
			logger.info("StudentController - addNewStudent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);			
		}	
	}
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateStudent(@Valid  @RequestBody StudentDto updateStudent, BindingResult result, @PathVariable Integer id) {
		
		logger.info("StudentController - updateStudent - starts.");
		if (result.hasErrors()) {
			logger.info("ClassController - addNewClass - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		if(updateStudent == null) {
			logger.info("StudentController - updateStudent -  student object for update is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
				HttpStatus.NOT_FOUND);
		}
		if(updateStudent.getClassId()==null && updateStudent.getDateEntered()== null && updateStudent.getDateOfBirth()==null && updateStudent.getFirstName()==null
				&& updateStudent.getJmbg()==null && updateStudent.getLastName()==null && updateStudent.getNote()==null) {
			logger.info("StudentController - updateStudent - student object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Student object isss invalid."),
					HttpStatus.BAD_REQUEST);
		}
		try {
			StudentEntity student = studentRepository.findById(id).orElse(null);

			if (student == null) {
				logger.info("StudentController - updateStudent - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
		
			if (StringUtils.isNotBlank(updateStudent.getFirstName())) {
				student.setFirstName(updateStudent.getFirstName());
			}
			if (StringUtils.isNotBlank(updateStudent.getLastName())) {
				student.setLastName(updateStudent.getFirstName());
			}
			if (StringUtils.isNotBlank(updateStudent.getJmbg())) {
				student.setJmbg(updateStudent.getJmbg());
			}
			if (updateStudent.getDateOfBirth() != null) {
				student.setDateOfBirth(updateStudent.getDateOfBirth());
			}
			if (StringUtils.isNotBlank(updateStudent.getNote())) {
				student.setNote(updateStudent.getNote());
			}
			if (updateStudent.getDateEntered() != null) {
				student.setDateEntered(updateStudent.getDateEntered());
			}

			if(updateStudent.getClassId()!=null) {
				ClassEntity newClass = classRepository.findById(updateStudent.getClassId()).orElse(null);
				if (newClass != null)
					student.setSchoolClass(newClass);
				}
			studentRepository.save(student);

			logger.info("StudentController - updateStudent - finished.");
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("StudentController - updateStudent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-class/{classId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> changeClassOfStudent(@PathVariable Integer id, @PathVariable Integer classId) {
		logger.info("StudentController - changeClassOfStudent - starts.");
		try {
			StudentEntity student = studentRepository.findById(id).orElse(null);
			ClassEntity newClass = classRepository.findById(classId).orElse(null);
			
			if (student == null) {
				logger.info("StudentController - changeClassOfStudent - student  not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (newClass == null) {
				logger.info("StudentController - changeClassOfStudent - class not found.");
				return new ResponseEntity<RESTError>(new RESTError("Class with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			student.setSchoolClass(newClass);		
			studentRepository.save(student);
		
			logger.info("StudentController - changeClassOfStudent - finished.");
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("StudentController - changeClassOfStudent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-parent/{parentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addParentToStudent(@PathVariable Integer id, @PathVariable Integer parentId) {
		logger.info("StudentController - addParentToStudent - starts.");
		try {
			ParentEntity parent = parentRepository.findById(parentId).orElse(null);
			StudentEntity student = studentRepository.findById(id).orElse(null);
			if (parent == null || student == null) {
				logger.info("StudentController - addParentToStudent - student and/or parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student or parent with provided ID not found."),
					HttpStatus.NOT_FOUND);
			}
			if(student.getParents().contains(parent)) {
				logger.info("StudentController - addParentToStudent - student already has that parent.");
				return new ResponseEntity<RESTError>(new RESTError("The parent already appears in the list of parents for that student."),
					HttpStatus.BAD_REQUEST);	
			}
		
			student.getParents().add(parent);
			studentRepository.save(student);
			logger.info("StudentController - addParentToStudent - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("StudentController - addParentToStudent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
		logger.info("StudentController - deleteStudent - starts.");
		try {
			StudentEntity student = studentRepository.findById(id).orElse(null);

			if (student == null) {
				logger.info("StudentController - deleteStudent - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
					HttpStatus.NOT_FOUND);
			}
			Iterable<GradingEntity> gradings = gradingRepository.findAll();
			for(GradingEntity grading : gradings) {
				if(grading.getStudent().equals(student)) {
					logger.info("StudentController - deleteStudent - student cannot be deleted because some grading has a reference to it.");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some grading has a reference to it"),
							HttpStatus.OK);
				}
			}
		
			Iterable<ParentEntity> parents = parentRepository.findAll();
			for(ParentEntity parent : parents) {
				if(parent.getStudents().contains(student)) {
					logger.info("StudentController - deleteStudent - cannot delete student because he is child of some parent.");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some parent has a reference to it"),
							HttpStatus.OK);
				}
			}
		
			Iterable<ClassEntity> classes = classRepository.findAll();
			for (ClassEntity schoolClass : classes) {
				if (schoolClass.getStudents().contains(student)) {
					logger.info("StudentController - deleteStudent - cannot delete student because some class has that student .");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some school class has a reference to it"),							
							HttpStatus.BAD_REQUEST);
				}
			}
		
			if(accountRepository.findByUserAndRole((UserEntity)student, EUserRole.ROLE_STUDENT)!=null) {
				logger.info("StudentController - deleteStudent - cannot delete student becouse hi had an account.");
				return new ResponseEntity<RESTError>(new RESTError("The student have account so you can't delete him."),HttpStatus.NOT_FOUND);
			}
			
			studentRepository.deleteById(id);
		
			logger.info("StudentController - deleteStudent - finished.");
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("StudentController - deleteStudent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "delete/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteStudentAndAccount(@PathVariable Integer id) {
		logger.info("StudentController - deleteStudentAndAccount - starts.");
		try {
			StudentEntity student = studentRepository.findById(id).orElse(null);

			if (student == null) {
				logger.info("StudentController - deleteStudentAndAccount - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
					HttpStatus.NOT_FOUND);
			}
			Iterable<GradingEntity> gradings = gradingRepository.findAll();
			for(GradingEntity grading : gradings) {
				if(grading.getStudent().equals(student)) {
					logger.info("StudentController - deleteStudentAndAccount - student cannot be deleted because some grading has a reference to it.");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some grading has a reference to it"),
						HttpStatus.BAD_REQUEST);
				}
			}
			Iterable<ParentEntity> parents = parentRepository.findAll();
			for(ParentEntity parent : parents) {
				if(parent.getStudents().contains(student)) {
					logger.info("StudentController - deleteStudentAndAccount - cannot delete student becouse he is child of some parent.");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some parent has a reference to it"),
						HttpStatus.BAD_REQUEST);
				}
			}

			Iterable<ClassEntity> classes = classRepository.findAll();
			for (ClassEntity schoolClass : classes) {
				if (schoolClass.getStudents().contains(student)) {
					logger.info("StudentController - deleteStudentAndAccount - cannot delete student becouse some class has that student .");
					return new ResponseEntity<RESTError>(new RESTError("A student cannot be deleted because some school class has a reference to it"),
						HttpStatus.BAD_REQUEST);
				}
			}
			
			AccountEntity account = accountRepository.findByUserAndRole((UserEntity)student, EUserRole.ROLE_STUDENT);
			UserEntity user = (UserEntity) student;
			user.getAccounts().remove(account);
			try {
				userRepository.save(user);
			} catch (Exception e) {
				logger.info("StudentController - deleteStudentAndAccount - Break on save user after remove account.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				studentRepository.delete(student);
			} catch (Exception e) {
				logger.info("AdministratorController - deleteStudentAndAccount - Can't delete student from student Repository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				accountRepository.delete(account);
			} catch (Exception e) {
				logger.info("AdministratorController - deleteStudentAndAccount - Can't delete account from accountRepository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			logger.info("StudentController - deleteStudentAndAccount - finished.");
			return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("StudentController - deleteStudentAndAccount - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}


	@Secured({"ROLE_ADMIN","ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value = "/by-parent/{parentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findStudentsByParentId(@PathVariable Integer parentId) {
		logger.info("StudentController - findStudentsByParentId - starts.");
		try {
			ParentEntity parent = parentRepository.findById(parentId).orElse(null);

			if (parent == null) {
				logger.info("StudentController - findStudentsByParentId - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<StudentEntity> students = new ArrayList();
				for(StudentEntity student : parent.getStudents())
					students.add(student);
				
				logger.info("StudentController - findStudentsByParentId - finished.");
				return new ResponseEntity<Iterable<StudentEntity>>(students, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("StudentController - findStudentsByParentId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by-schoolClass/{schoolClassId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findStudentsBySchoolClassId(@PathVariable Integer schoolClassId) {
		logger.info("StudentController - findStudentsBySchoolClassId - starts.");
		try {
			ClassEntity schoolClass = classRepository.findById(schoolClassId).orElse(null);

			if (schoolClass == null) {
				logger.info("StudentController - findStudentsBySchoolClassId - class not found.");
				return new ResponseEntity<RESTError>(new RESTError("School class with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<StudentEntity> students = new ArrayList();
				students.addAll( schoolClass.getStudents());
				logger.info("StudentController - findStudentsBySchoolClassId - finished.");
				return new ResponseEntity<Iterable<StudentEntity>>(students, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("StudentController - findStudentsBySchoolClassId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateStudent(@PathVariable Integer id) {
		logger.info("StudentController - deactivateStudent - starts.");

		StudentEntity student = studentRepository.findById(id).orElse(null);

		if (student == null) {
			return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		UserEntity user = (UserEntity)student;
		AccountEntity account = accountRepository.findByUserAndRole(user, EUserRole.ROLE_STUDENT);
		account.setIsActive(false);
		studentRepository.delete(student);
		accountRepository.save(account);
		
		logger.info("StudentController - deactivateStudent - finished.");
		return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
	}
	
}
