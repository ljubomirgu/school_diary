package com.iktpreobuka.test.controllers;

import java.util.ArrayList;
import java.util.List;
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
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.TeacherDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.AccountDao;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/teachers")
public class TeacherController {
	
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRepository accountRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllTeachers() {
		logger.info("TeacherController - getAllTeachers - starts.");

		return new ResponseEntity<Iterable<TeacherEntity>>(teacherRepository.findAll(), HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findTeacherById(@PathVariable Integer id) {
		logger.info("TeacherController - findTeacherById - starts.");
		try {
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);

			if (teacher == null) {
				logger.info("TeacherController - findTeacherById - teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("TeacherController - findTeacherById - finished.");
				return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("TeacherController - findTeacherById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@SuppressWarnings("null")
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewTeacher(@Valid @RequestBody TeacherDto newTeacher, BindingResult result) {
		logger.info("TeacherController - addNewTeacher - starts.");
		
		if(result.hasErrors()) {
			logger.info("TeacherController - addNewTeacher - starts.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);		
		}
	
		if (newTeacher == null) {
			logger.info("TeacherController - addNewTeacher - starts.");
			return new ResponseEntity<RESTError>(new RESTError("Teacher object is invalid."),
					HttpStatus.BAD_REQUEST);
		}
		try {
			List<SubjectEntity> subjects = new ArrayList<>();
			for(Integer subjectId : newTeacher.getSubjectsId()) {	
				logger.info("TeacherController - addNewTeacher - starts.");
				SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
				if(subject != null) {				
					subjects.add(subject);
				}
			}
		
			if(subjects.isEmpty()) { 
				logger.info("TeacherController - addNewTeacher - starts.");
				return new ResponseEntity<RESTError>(new RESTError("the subjects taught by the teacher do not exist in the school."),
						HttpStatus.BAD_REQUEST);		
			}
		
			if (newTeacher.getFirstName() == null || newTeacher.getLastName() == null || newTeacher.getDateOfBirth() == null
					|| newTeacher.getJmbg() == null || newTeacher.getDateOfEmployment() == null ||	newTeacher.getVocation() == null
					|| newTeacher.getUsername()==null || newTeacher.getPassword()==null) {
				logger.info("TeacherController - addNewTeacher - teacher object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher object is invalid."),
						HttpStatus.BAD_REQUEST);
			}	
		
			UserEntity user = userRepository.findByJmbg(newTeacher.getJmbg());
			if(user != null) {
			
				TeacherEntity teacherExists = teacherRepository.findById(user.getUserId()).orElse(null);
			
				if(teacherExists != null) {
					logger.info("TeacherController - addNewTeacher - jmbg is already in the database.");
					return new ResponseEntity<RESTError>(new RESTError("Teacher with that jmbg already exists."),HttpStatus.BAD_REQUEST);
				}

				if(!( user.getFirstName().equals(newTeacher.getFirstName()) && user.getLastName().equals(newTeacher.getLastName()) && user.getDateOfBirth().equals(newTeacher.getDateOfBirth()))) {
					logger.info("TeacherController - addNewTeacher - jmbg is already in the database.");
					return new ResponseEntity<RESTError>(new RESTError("There is another person with same jmbg."),HttpStatus.BAD_REQUEST);	
				}
				
				StudentEntity studentt = studentRepository.findById(user.getUserId()).orElse(null);
				if(studentt != null) {
					logger.info("TeacherController - addNewTeacher - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("There is already a student with same jmbg, and the student cannot have another role at the same time."),HttpStatus.BAD_REQUEST);
				}
			
				try {
					teacherRepository.insertNewTeacher(newTeacher.getDateOfEmployment(),newTeacher.getVocation(), user.getUserId());
				}
				catch(Exception e){
					logger.info("TeacherController - addNewTeacher - insert query is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("Query is invalid"),HttpStatus.BAD_REQUEST);
				}		
			
				try {	
					accountDao.createAndSaveAccount(newTeacher.getUsername(), newTeacher.getPassword(), user, EUserRole.ROLE_TEACHER);				
				}
				catch (Exception e) {
					teacherRepository.deleteById(user.getUserId());
					logger.info("TeacherController - addNewTeacher - teacher and his account not created.");
					return new ResponseEntity<RESTError>(new RESTError("Account for teacher role not created and teacher not added"),	HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
				for(SubjectEntity subject : subjects) {
					teacherRepository.insertNewTeacherSubject(subject.getSubjectId(), user.getUserId());
				}
				logger.info("TeacherController - addNewTeacher - finished.");
				return new ResponseEntity<>("Teacher was successfully recorded", HttpStatus.OK);	
				
			}	
			
			TeacherEntity teacher = new TeacherEntity();
			teacher.setFirstName(newTeacher.getFirstName());
			teacher.setLastName(newTeacher.getLastName());
			teacher.setJmbg(newTeacher.getJmbg());
			teacher.setDateOfEmployment(newTeacher.getDateOfEmployment());
			teacher.setDateOfBirth(newTeacher.getDateOfBirth());
			teacher.setVocation(newTeacher.getVocation());
			teacher.setSubjects(subjects);
		
			teacherRepository.save(teacher);
			UserEntity userT = (UserEntity)teacher;
				
			try {	
				accountDao.createAndSaveAccount(newTeacher.getUsername(), newTeacher.getPassword(), userT, EUserRole.ROLE_TEACHER);
			}
			catch (Exception e) {
				teacherRepository.deleteById(user.getUserId());
				logger.info("TeacherController - addNewTeacher - teacher and his account not created.");
				return new ResponseEntity<RESTError>(new RESTError("Account not created and teacher is deleted."),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		
			logger.info("TeacherController - addNewTeacher - finished.");
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);	
		}
		catch (Exception e){
			logger.info("TeacherController - addNewTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}


	@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateTeacher(@Valid @RequestBody TeacherDto updateTeacher, BindingResult result,
			@PathVariable Integer id) {
		logger.info("TeacherController - updateTeacher - starts.");
		if(result.hasErrors()) {
			logger.info("TeacherController - addNewTeacher - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);		
		}
		try {
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);

			if (teacher == null || updateTeacher == null) {
				logger.info("TeacherController - updateTeacher - teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (StringUtils.isNotBlank(updateTeacher.getFirstName())) {
				teacher.setFirstName(updateTeacher.getFirstName());
			}
			if (StringUtils.isNotBlank(updateTeacher.getLastName())) {
				teacher.setLastName(updateTeacher.getFirstName());
			}
			if (StringUtils.isNotBlank(updateTeacher.getJmbg())) {
				teacher.setJmbg(updateTeacher.getJmbg());
			}
			if (StringUtils.isNotBlank(updateTeacher.getVocation())) {
				teacher.setVocation(updateTeacher.getVocation());
			}
			if (updateTeacher.getDateOfEmployment() != null) {
				teacher.setDateOfEmployment(updateTeacher.getDateOfEmployment());
			}
			if (updateTeacher.getDateOfBirth() != null) {
				teacher.setDateOfBirth(updateTeacher.getDateOfBirth());
			}

			teacherRepository.save(teacher);

			logger.info("TeacherController - updateTeacher - finished.");
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("TeacherController - updateTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addSubjectToTeacher(@PathVariable Integer id, @PathVariable Integer subjectId) {
		logger.info("TeacherController - addSubjectToTeacher - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);
			if (subject == null || teacher == null) {
				logger.info("TeacherController - addSubjectToTeacher - teacher and/or subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher or subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (teacher.getSubjects().contains(subject)) {
				logger.info("TeacherController - addSubjectToTeacher - teacher already has that subject in his list of subjects.");
				return new ResponseEntity<RESTError>(
						new RESTError("The subject already appears in the list of subjects."), HttpStatus.BAD_REQUEST);
			}

			teacher.getSubjects().add(subject);
			teacherRepository.save(teacher);

			logger.info("TeacherController - addSubjectToTeacher - finished.");
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("TeacherController - addSubjectToTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-lecture/{lectureId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addLectureToTeacher(@PathVariable Integer id, @PathVariable Integer lectureId) {
		logger.info("TeacherController - addLectureToTeacher - starts.");
		try {
			LectureEntity lecture = lectureRepository.findById(lectureId).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);
			if (lecture == null || teacher == null) {
				logger.info("TeacherController - addLectureToTeacher - teacher and/or lecture not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher or lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (teacher.getLectures().contains(lecture)) {
				logger.info("TeacherController - addLectureToTeacher - teacher already has that lecture in his list of lectures.");
				return new ResponseEntity<RESTError>(
						new RESTError("The lecture already appears in the list of lectures."), HttpStatus.BAD_REQUEST);
			}
			if (teacher.getSubjects().contains(lecture.getSubject())) {
				if (lecture.getSubject().getYears().contains(lecture.getSchoolClass().getYear())) {
					teacher.getLectures().add(lecture);
					teacherRepository.save(teacher);

					logger.info("TeacherController - addLectureToTeacher - finished.");
					return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
				}
			}
			logger.info("TeacherController - addLectureToTeacher - lecture object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		} 
		catch (Exception e) {
			logger.info("TeacherController - addLectureToTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteTeacher(@PathVariable Integer id) {
		logger.info("TeacherController - deleteTeacher - starts.");
		try {
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);

			if (teacher == null) {
				return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			
			Iterable<LectureEntity> lectures = lectureRepository.findAll();
			for (LectureEntity lecture : lectures) {
				if (lecture.getTeacher().equals(teacher))
					return new ResponseEntity<RESTError>(
							new RESTError("Teacher cannot be deleted because some lecture has a reference to it"),
							HttpStatus.BAD_REQUEST);
			}
			Iterable<SubjectEntity> subjects = subjectRepository.findAll();
			for (SubjectEntity subject : subjects) {
				if (subject.getTeachers().contains(teacher))
					return new ResponseEntity<RESTError>(
							new RESTError("Teacher cannot be deleted because some subject has a reference to it"),
							HttpStatus.BAD_REQUEST);
			}

			if(accountRepository.findByUserAndRole((UserEntity)teacher, EUserRole.ROLE_TEACHER)!=null)
				return new ResponseEntity<RESTError>(new RESTError("The teacher have account so you can't delete him."),HttpStatus.BAD_REQUEST);

			AccountEntity account = accountRepository.findByUserAndRole((UserEntity)teacher, EUserRole.ROLE_TEACHER);

			try {
				teacherRepository.delete(teacher);
			}
			catch(Exception e) {
				return new ResponseEntity<RESTError>(new RESTError("Problem when try delete teacher wich has account"),HttpStatus.BAD_REQUEST);
			}
			try {
				accountRepository.delete(account);
			}
			catch(Exception e) {
				return new ResponseEntity<RESTError>(new RESTError("Problem when try delete account that has user"),HttpStatus.BAD_REQUEST);
			}

			teacherRepository.deleteById(id);
		
			logger.info("TeacherController - deleteTeacher - finished.");
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
	}
	catch (Exception e){
		logger.info("TeacherController - deleteTeacher - finished.");
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "delete/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteTeacherAndAccount(@PathVariable Integer id) {
		logger.info("TeacherController - deleteTeacher - starts.");
		try {
			TeacherEntity teacher = teacherRepository.findById(id).orElse(null);

			if (teacher == null) {
				logger.info("TeacherController - deleteTeacherAndAccount - teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			Iterable<LectureEntity> lectures = lectureRepository.findAll();
			for (LectureEntity lecture : lectures) {
				if (lecture.getTeacher().equals(teacher)) {
					logger.info("TeacherController - deleteTeacherAndAccount - cannot delete, some teacher references it.");
					return new ResponseEntity<RESTError>(
							new RESTError("Teacher cannot be deleted because some lecture has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}
			Iterable<SubjectEntity> subjects = subjectRepository.findAll();
			for (SubjectEntity subject : subjects) {
				if (subject.getTeachers().contains(teacher)) {
					logger.info("TeacherController - deleteTeacherAndAccount - can't delete, some subject references it.");
					return new ResponseEntity<RESTError>(
							new RESTError("Teacher cannot be deleted because some subject has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			AccountEntity account = accountRepository.findByUserAndRole((UserEntity)teacher, EUserRole.ROLE_TEACHER);
			UserEntity user = (UserEntity)teacher;
			user.getAccounts().remove(account);
			try {
				userRepository.save(user);
			} catch (Exception e) {
				logger.info("StudentController - deleteTeacherAndAccount - Break on save user after remove account.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				teacherRepository.delete(teacher);
			}
			catch(Exception e) {
				logger.info("TeacherController - deleteTeacherAndAccount - delete teacher error.");
				return new ResponseEntity<RESTError>(new RESTError("Problem when try delete teacher wich has account"),HttpStatus.BAD_REQUEST);
			}
			try {
				accountRepository.delete(account);
			}
			catch(Exception e) {
				logger.info("TeacherController - deleteTeacherAndAccount - delete account error.");
				return new ResponseEntity<RESTError>(new RESTError("Problem when try delete account that has user"),HttpStatus.BAD_REQUEST);
			}

			logger.info("TeacherController - deleteTeacherAndAccount - finished.");
			return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("TeacherController - deleteTeacherAndAccount - finished.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findTeachersBySubjectId(@PathVariable Integer subjectId) {
		logger.info("TeacherController - findTeachersBySubjectId - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);

			if (subject == null) {
				logger.info("TeacherController - findTeachersBySubjectId - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<TeacherEntity> teachers = new ArrayList();
				for(TeacherEntity teacher : subject.getTeachers())
					teachers.add(teacher);
				
				logger.info("TeacherController - findTeachersBySubjectId - finished.");
				return new ResponseEntity<Iterable<TeacherEntity>>(teachers, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("TeacherController - findTeachersBySubjectId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")	
	//@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateTeacher(@PathVariable Integer id) {
		logger.info("TeacherController - deactivateTeacher - starts.");

		TeacherEntity teacher = teacherRepository.findById(id).orElse(null);

		if (teacher == null) {
			return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		UserEntity user = (UserEntity)teacher;
		AccountEntity account = accountRepository.findByUserAndRole(user, EUserRole.ROLE_TEACHER);
		account.setIsActive(false);
		accountRepository.save(account);
		teacherRepository.deleteById(id);
		
		logger.info("TeacherController - deactivateTeacher - finished.");
		return new ResponseEntity<TeacherEntity>(teacher, HttpStatus.OK);
	}


}
