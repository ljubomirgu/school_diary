package com.iktpreobuka.test.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.dto.GradingDto;
import com.iktpreobuka.test.entities.dto.GradingDtoNoTeacherId;
import com.iktpreobuka.test.enumerations.ESemester;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.models.EmailObject;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.repositories.GradingRepository;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.ParentRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.EmailService;
import com.iktpreobuka.test.services.GradingDao;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/gradings")
public class GradingController {

	@Autowired
	private GradingRepository gradingRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private ParentRepository parentRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private GradingDao gradingDao;
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AccountRepository accountRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllGradings() {
		logger.info("GradingController - getAllGradings - starts.");

		return new ResponseEntity<Iterable<GradingEntity>>(gradingRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findGradingById(@PathVariable Integer id) {
		logger.info("GradingController - findGradingById - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - findGradingById - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("GradingController - findGradingById - finished.");
				return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
			}
		} 
		catch (Exception e) {
			logger.info("GradingController - findGradingById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

		@Secured("ROLE_STUDENT")
		@RequestMapping(method = RequestMethod.GET, value = "/student")
//		@JsonView(Views.Student.class)
		public ResponseEntity<?> getGradingsForStudent(Principal principal) {
			logger.info("GradingController - getGradingsByStudent - starts.");
			try {
				AccountEntity account = accountRepository.findByUsername(principal.getName());
				StudentEntity student = studentRepository.findById(account.getUser().getUserId()).orElse(null);
				
				if(student == null) {
					logger.info("GradingController - getGradingsByStudent - student not found.");
					return new ResponseEntity<RESTError>(new RESTError("Student not found."),
							HttpStatus.NOT_FOUND);
				}
	
				logger.info("GradingController - getGradingsForStudent - finished.");
				return new ResponseEntity<Iterable<GradingEntity>>(gradingRepository.findByStudent(student), HttpStatus.OK);
			} 
			catch (Exception e) {
				logger.info("GradingController - getGradingsByStudent - intrenal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@Secured("ROLE_STUDENT")
		@RequestMapping(method = RequestMethod.GET, value = "/student/{gradeId}")
//		@JsonView(Views.Student.class)
		public ResponseEntity<?> getGradingByIdForStudent(@PathVariable Integer gradeId,Principal principal) {
			logger.info("GradingController - getGradingByIdForStudent - starts.");
			try {
				AccountEntity account = accountRepository.findByUsername(principal.getName());
				StudentEntity student = studentRepository.findById(account.getUser().getUserId()).orElse(null);
				
				if(student == null) {
					logger.info("GradingController - getGradingByIdForStudent - student not found.");
					return new ResponseEntity<RESTError>(new RESTError("Student not found."),
							HttpStatus.NOT_FOUND);
				}
				
				GradingEntity grading = gradingRepository.findById(gradeId).orElse(null);
				if(grading == null) {
					logger.info("GradingController - getGradingByIdForStudent - grading not found.");
					return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
							HttpStatus.NOT_FOUND);
				}
				
				if(!(grading.getStudent().equals(student))) {
					logger.info("GradingController - getGradingByIdForStudent - you don't have permission.");
					return new ResponseEntity<RESTError>(new RESTError("You don't have permission."),
							HttpStatus.FORBIDDEN);
				}
	
				logger.info("GradingController - getGradingByIdForStudent - finished.");
				return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
			}
			catch (Exception e) {
				logger.info("GradingController - getGradingByIdForStudent - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
			

	@Secured("ROLE_PARENT")
	@RequestMapping(method = RequestMethod.GET, value = "/parent")
//	@JsonView(Views.Parent.class)
	public ResponseEntity<?> getGradingsForParent(Principal principal) {
		logger.info("GradingController - getGradingsForParent - starts.");
		try {
			AccountEntity account = accountRepository.findByUsername(principal.getName());
			if (!account.getRole().equals(EUserRole.ROLE_PARENT)) {
				logger.info("GradingController - getGradingsForParent - you don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			ParentEntity parent = parentRepository.findById(account.getUser().getUserId()).orElse(null);
			if (parent == null) {
				logger.info("GradingController - getGradingsForParent - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent not found."), HttpStatus.NOT_FOUND);
			}

			logger.info("GradingController - getGradingsForParent - finished.");
			 return new	 ResponseEntity<Iterable<GradingEntity>>(gradingRepository.findByParent(parent), HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - getGradingsForParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_PARENT")
	@RequestMapping(method = RequestMethod.GET, value = "/parent/{gradeId}")
//	@JsonView(Views.Parent.class)
	public ResponseEntity<?> getGradingByIdForParent(@PathVariable Integer gradeId, Principal principal) {
		logger.info("GradingController - getGradingByIdForParent - starts.");
		try {
			AccountEntity account = accountRepository.findByUsername(principal.getName());
			if (!account.getRole().equals(EUserRole.ROLE_PARENT)) {
				logger.info("GradingController - getGradingByIdForParent - you don't have permis.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			ParentEntity parent = parentRepository.findById(account.getUser().getUserId()).orElse(null);
			if (parent == null) {
				logger.info("GradingController - getGradingByIdForParent - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent not found."), HttpStatus.NOT_FOUND);
			}

			GradingEntity grading = gradingRepository.findById(gradeId).orElse(null);
			if (grading == null) {
				logger.info("GradingController - getGradingByIdForParent - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (!(grading.getStudent().getParents().contains(parent))) {
				logger.info("GradingController - getGradingByIdForParent - zou don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			logger.info("GradingController - getGradingByIdForParent - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - getGradingByIdForParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher1")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> getGradingsForTeacher1(Principal principal) {
		logger.info("GradingController - getGradingsForTeacher - starts.");
		try {
			AccountEntity account = accountRepository.findByUsername(principal.getName());
			if (!account.getRole().equals(EUserRole.ROLE_TEACHER)) {
				logger.info("GradingController - getGradingsForTeacher - you don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			TeacherEntity teacher = teacherRepository.findById(account.getUser().getUserId()).orElse(null);
			if (teacher == null) {
				logger.info("GradingController - getGradingsForTeacher - teacher  not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher not found."), HttpStatus.NOT_FOUND);
			}

			logger.info("GradingController - getGradingsForTeacher - finished.");
	
			 return new	 ResponseEntity<Iterable<GradingEntity>>(gradingRepository.findByTeacher(teacher), HttpStatus.OK);
		
			} 
		catch (Exception e) {
			logger.info("GradingController - getGradingsForTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


		@Secured("ROLE_TEACHER")
		@RequestMapping(method = RequestMethod.GET, value = "/teacher")
//		@JsonView(Views.Teacher.class)
		public ResponseEntity<?> getGradingsForTeacher(Principal principal) {
			logger.info("GradingController - getGradingsForTeacher - starts.");
			try {
				AccountEntity account = accountRepository.findByUsername(principal.getName());
				if (!account.getRole().equals(EUserRole.ROLE_TEACHER)) {
					logger.info("GradingController - getGradingsForTeacher - you don't have permission.");
					return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
				}

				TeacherEntity teacher = teacherRepository.findById(account.getUser().getUserId()).orElse(null);
				if (teacher == null) {
					logger.info("GradingController - getGradingsForTeacher - teacher  not found.");
					return new ResponseEntity<RESTError>(new RESTError("Teacher not found."), HttpStatus.NOT_FOUND);
				}

				logger.info("GradingController - getGradingsForTeacher - finished.");

				 return new	 ResponseEntity<Iterable<GradingEntity>>(gradingRepository.findByTeacher(teacher), HttpStatus.OK);

			} 
			catch (Exception e) {
				logger.info("GradingController - getGradingsForTeacher - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.GET, value = "/teacher/{gradeId}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> getGradingByIdForTeacher(@PathVariable Integer gradeId, Principal principal) {
		logger.info("GradingController - getGradingByIdForTeacher - starts.");
		try {
			AccountEntity account = accountRepository.findByUsername(principal.getName());
			if (!account.getRole().equals(EUserRole.ROLE_TEACHER)) {
				logger.info("GradingController - getGradingByIdForTeacher - you don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			TeacherEntity teacher = teacherRepository.findById(account.getUser().getUserId()).orElse(null);
			if (teacher == null) {
				logger.info("GradingController - getGradingByIdForTeacher - teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("Teacher not found."), HttpStatus.NOT_FOUND);
			}

			GradingEntity grading = gradingRepository.findById(gradeId).orElse(null);
			if (grading == null) {
				logger.info("GradingController - getGradingByIdForTeacher - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (!(grading.getLecture().getTeacher().equals(teacher))) {
				logger.info("GradingController - getGradingByIdForTeacher - you don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}
			logger.info("GradingController - getGradingByIdForTeacher - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - getGradingByIdForTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}

	
	@SuppressWarnings("unused")
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> addNewGrading(@Valid @RequestBody GradingDtoNoTeacherId newGrading, BindingResult result, Principal principal) {
		logger.info("GradingController - addNewGrading - starts.");
					
		if(result.hasErrors()) {
			logger.info("GradingController - addNewGrading - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
	
		if (newGrading == null) {
			logger.info("GradingController - addNewGrading - grading is null.");
			return new ResponseEntity<RESTError>(new RESTError("Grading object is invalid."),
					HttpStatus.BAD_REQUEST);
		}
		if(newGrading.getGrade()==null || newGrading.getSemester()==null || newGrading.getStudentId()==null 
				|| newGrading.getSubjectId()==null) {
			logger.info("GradingController - addNewGrading - some of atributes of gradings is null.");
			return new ResponseEntity<RESTError>(new RESTError("Grading object is invalid."),
					HttpStatus.BAD_REQUEST);
		}
			
		try {
			AccountEntity account = accountRepository.findByUsername(principal.getName());
			
			TeacherEntity teacher = teacherRepository.findById(account.getUser().getUserId()).orElse(null);
			if(teacher == null) {
			logger.info("GradingController - addNewGrading - teacher not found.");
			return new ResponseEntity<RESTError>(new RESTError("Teacher not found."),
					HttpStatus.NOT_FOUND);
			}
		
			
			StudentEntity student = studentRepository.findById(newGrading.getStudentId()).orElse(null);
			if(student == null) {
				logger.info("GradingController - addNewGrading - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student not found."),
						HttpStatus.NOT_FOUND);
			}
		
			SubjectEntity subject = subjectRepository.findById(newGrading.getSubjectId()).orElse(null);
			if(subject == null) {
				logger.info("GradingController - addNewGrading - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject not found."),
						HttpStatus.NOT_FOUND);
			}

			if(!(newGrading.getSemester().equals(ESemester.FIRST_SEMESTER.toString()) || newGrading.getSemester().equals(ESemester.SECOND_SEMESTER.toString()))) {
				logger.info("GradingController - addNewGrading - wrong value of semester.");
					return new ResponseEntity<RESTError>(new RESTError("Semestar must be FIRST_SEMESTER or SECOND_SEMESTER."),
							HttpStatus.BAD_REQUEST);
			}
		
			if(newGrading.getGrade()<1 || newGrading.getGrade()>5) {
				logger.info("GradingController - addNewGrading - wrong value of grade.");
				return new ResponseEntity<RESTError>(new RESTError("Grade must be between 1 and 5."),
						HttpStatus.BAD_REQUEST);
			}

			LectureEntity lecture = lectureRepository.findBySchoolClassAndSubjectAndTeacher(student.getSchoolClass(), subject, teacher);
		
			if(lecture == null) {
				logger.info("GradingController - addNewGrading - That professor does not teach that subject to that department.");
				return new ResponseEntity<RESTError>(new RESTError("That professor does not teach that subject to that student."),
						HttpStatus.BAD_REQUEST);
			}
			
			GradingEntity grading = new GradingEntity();
			grading.setGrade(newGrading.getGrade());
			grading.setSemester(ESemester.valueOf(newGrading.getSemester()));
			grading.setLecture(lecture);
			grading.setStudent(student);
		
			gradingRepository.save(grading);

			String emailSubject = "New grade";		
			String text ="The student " +student.getFirstName().toString()+" "+student.getLastName().toString()+" received a grade "
					+grading.getGrade().toString()+" from school subject "	+subject.getSubjectName().toString()+". The grade was given by a teacher " 
					+grading.getLecture().getTeacher().getFirstName()+ " "+grading.getLecture().getTeacher().getLastName()+".";
			List<ParentEntity> parents = student.getParents();
			for(ParentEntity parent : parents) {		
				EmailObject object = new EmailObject();
				object.setSubject(emailSubject);
				object.setText(text);
				object.setTo(parent.getEmail());
				emailService.sendSimpleMessage(object); 
			}
		
		
			logger.info("GradingController - addNewGrading - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("GradingController - addNewGrading - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-value-by-teacher/{grade}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateValueOfGradeByTeacher(@PathVariable Integer id, @PathVariable Integer grade,
			Principal principal) {
		logger.info("GradingController - updateValueOfGradeByTeacher - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateValueOfGradeByTeacher - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			AccountEntity account = accountRepository.findByUsername(principal.getName());

			if (account.getUser().getUserId() != grading.getLecture().getTeacher().getUserId()) {
				logger.info("GradingController - updateValueOfGradeByTeacher - the teacher does not have a permit.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			if (grade < 1 || grade > 5) {
				logger.info("GradingController - updateValueOfGradeByTeacher - wrong value of grade.");
				return new ResponseEntity<RESTError>(new RESTError("Grade must be between 1 and 5."),HttpStatus.BAD_REQUEST);
			}

			grading.setGrade(grade);
			gradingRepository.save(grading);

			logger.info("GradingController - updateValueOfGradeByTeacher - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - updateValueOfGradeByTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-value-by-admin/{grade}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateValueOfGradeByAdmin(@PathVariable Integer id, @PathVariable Integer grade,
			Principal principal) {
		logger.info("GradingController - updateValueOfGradeByAdmin - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateValueOfGradeByAdmin - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (grade < 1 || grade > 5) {
				logger.info("GradingController - updateValueOfGradeByAdmin - wrong value of grade.");
				return new ResponseEntity<RESTError>(new RESTError("Grade must be between 1 and 5."),
						HttpStatus.BAD_REQUEST);
			}

			grading.setGrade(grade);
			gradingRepository.save(grading);

			logger.info("GradingController - updateValueOfGradeByAdmin - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - updateGradingByTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-semester-by-teacher/{semester}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateSemesterOfGradeByTeacher(@PathVariable Integer id, @PathVariable String semester,
			Principal principal) {
		logger.info("GradingController - updateSemesterOfGradeByTeacher - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateSemesterOfGradeByTeacher - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			AccountEntity account = accountRepository.findByUsername(principal.getName());

			if (account.getUser().getUserId() != grading.getLecture().getTeacher().getUserId()) {
				logger.info("GradingController - updateSemesterOfGradeByTeacher - teacher don't has permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			if (!(semester.equals(ESemester.FIRST_SEMESTER.toString())
					|| semester.equals(ESemester.SECOND_SEMESTER.toString()))) {
				logger.info("GradingController - updateSemesterOfGradeByTeacher - wrong value of semester.");
				return new ResponseEntity<RESTError>(
						new RESTError("Semestar must be FIRST_SEMESTER or SECOND_SEMESTER."), HttpStatus.BAD_REQUEST);
				}

			grading.setSemester(ESemester.valueOf(semester));
			gradingRepository.save(grading);

			logger.info("GradingController - updateSemesterOfGradeByTeacher - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - updateSemesterOfGradeByTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-semester-by-admin/{semester}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateSemesterOfGradeByAdmin(@PathVariable Integer id, @PathVariable String semester) {
		logger.info("GradingController - updateSemesterOfGradeByAdmin - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateSemesterOfGradeByAdmin - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (!(semester.equals(ESemester.FIRST_SEMESTER.toString())|| semester.equals(ESemester.SECOND_SEMESTER.toString()))) {
				logger.info("GradingController - updateSemesterOfGradeByAdmin - wrong value of semester.");
				return new ResponseEntity<RESTError>(
						new RESTError("Semestar must be FIRST_SEMESTER or SECOND_SEMESTER."), HttpStatus.BAD_REQUEST);
			}

			grading.setSemester(ESemester.valueOf(semester));
			gradingRepository.save(grading);

			logger.info("GradingController - updateSemesterOfGradeByAdmin - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - updateSemesterOfGradeByAdmin - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-student-by-teacher/{studentId}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateStudentOfGradingByTeacher(@PathVariable Integer id, @PathVariable Integer studentId,
			Principal principal) {
		logger.info("GradingController - updateStudentOfGradingByTeacher - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateStudentOfGradingByTeacher - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			StudentEntity student = studentRepository.findById(studentId).orElse(null);

			AccountEntity account = accountRepository.findByUsername(principal.getName());

			if (account.getUser().getUserId() != grading.getLecture().getTeacher().getUserId()) {
				logger.info("GradingController - updateStudentOfGradingByTeacher - you don't have permission.");
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."), HttpStatus.FORBIDDEN);
			}

			if (!student.getSchoolClass().equals(grading.getLecture().getSchoolClass())) {
				logger.info("GradingController - updateStudentOfGradingByTeacher - update student is from other department.");
				return new ResponseEntity<RESTError>(new RESTError("Update student is from other department."), HttpStatus.BAD_REQUEST);
			}
			grading.setStudent(student);
			gradingRepository.save(grading);

			logger.info("GradingController - updateStudentOfGradingByTeacher - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - updateStudentOfGradingByTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/change-student-by-admin/{studentId}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateStudentOfGradingByAdmin(@PathVariable Integer id, @PathVariable Integer studentId) {
		logger.info("GradingController - updateStudentOfGradingByAdmin - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - updateStudentOfGradingByAdmin - grading not found.");
				return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			StudentEntity student = studentRepository.findById(studentId).orElse(null);

			if (!student.getClass().equals(grading.getClass())) {
				logger.info("GradingController - updateStudentOfGradingByAdmin - wrong student.");
				return new ResponseEntity<RESTError>(new RESTError("Update student is from other department."),
						HttpStatus.BAD_REQUEST);
			}

			gradingRepository.save(grading);

			logger.info("GradingController - updateStudentOfGradingByAdmin - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.info("GradingController - updateStudentOfGradingByAdmin - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@SuppressWarnings("unused")
	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.PUT,value = "/{id}/by-teacher")///promenaocene/{value}")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateGradingByTeacher( @PathVariable Integer id, @Valid @RequestBody GradingDto newGrading, BindingResult result, Principal principal) {
		logger.info("GradingController - updateGradingByTeacher - starts.");
		
		GradingEntity grading = gradingRepository.findById(id).orElse(null);		

		if (grading == null || newGrading == null) {
			return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}

		AccountEntity account = accountRepository.findByUsername(principal.getName());

		if(account.getUser().getUserId() != grading.getLecture().getTeacher().getUserId())
				return new ResponseEntity<RESTError>(new RESTError("You don't have permission."),
						HttpStatus.FORBIDDEN);

		if(newGrading.getTeacherId()!=null) {
			return new ResponseEntity<RESTError>(new RESTError("You can't change teacher."),
					HttpStatus.BAD_REQUEST);
		}
		
		if(newGrading.getGrade()==null && newGrading.getSemester()==null && newGrading.getStudentId()==null &&
				newGrading.getSubjectId()==null) {
			return new ResponseEntity<RESTError>(new RESTError("New grading object is invalid."),
					HttpStatus.BAD_REQUEST);
		}
		
		if(newGrading.getGrade()!=null)
			grading.setGrade(newGrading.getGrade());
		if(newGrading.getSemester()!=null &&(newGrading.getSemester().equals(ESemester.FIRST_SEMESTER.toString()) || newGrading.getSemester().equals(ESemester.SECOND_SEMESTER.toString())) )
			grading.setSemester(ESemester.valueOf(newGrading.getSemester()));
		if(newGrading.getStudentId()!=null && newGrading.getSubjectId()==null) {
			StudentEntity rightStudent = studentRepository.findById(newGrading.getStudentId()).orElse(null);
			if(rightStudent==null)
					return new ResponseEntity<RESTError>(new RESTError("Update grading object is invalid,student with that id not found."),HttpStatus.BAD_REQUEST);
			if(grading.getLecture().getSchoolClass().getStudents().contains(rightStudent))
				grading.setStudent(rightStudent);
				
		}
				
		if(newGrading.getStudentId()!=null) {
			StudentEntity student = studentRepository.findById(newGrading.getStudentId()).orElse(null);
			if(student!=null) {
		//		if()
			}
		}
		if(newGrading.getGrade()!=null && newGrading.getGrade()<6 && newGrading.getGrade()>0)
			grading.setGrade(newGrading.getGrade());
		
		if(newGrading.getSemester()!= null) {
			if(ESemester.valueOf(newGrading.getSemester()).equals(ESemester.FIRST_SEMESTER) || ESemester.valueOf(newGrading.getSemester()).equals(ESemester.FIRST_SEMESTER))
				grading.setSemester(ESemester.valueOf(newGrading.getSemester()));
		}
	
		return new ResponseEntity<RESTError>(new RESTError("Grading object is invalid."),
				HttpStatus.BAD_REQUEST);
		
	}
	

	@SuppressWarnings("unused")
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT,value = "/{id}/by-admin")
//	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> updateGradingByAdmin(@PathVariable Integer id, @RequestBody GradingEntity newGrading,
			Principal principal) {
		logger.info("GradingController - updateGradingByAdmin - starts.");

		GradingEntity grading = gradingRepository.findById(id).orElse(null);

		if (grading == null || newGrading == null) {
			return new ResponseEntity<RESTError>(new RESTError("Grading with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		
		if(newGrading.getGrade()<1 || newGrading.getGrade()>5)
			return new ResponseEntity<RESTError>(new RESTError("Grade must be between 1 and 5."),
					HttpStatus.BAD_REQUEST);

		if (grading.getLecture().equals(newGrading.getLecture()) && grading.getSemester().equals(newGrading.getSemester())
				&& grading.getStudent().equals(newGrading.getStudent())) {
			grading.setGrade(newGrading.getGrade());
			gradingRepository.save(grading);

			logger.info("GradingController - addNewGrading - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		}
			
		return new ResponseEntity<RESTError>(new RESTError("Grading object is invalid."),
					HttpStatus.BAD_REQUEST);
			
	}
		
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}/admin")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteGradingByAdmin(@PathVariable Integer id) {
		logger.info("GradingController - deleteGradingByAdmin - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - deleteGradingByAdmin - admin not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			gradingRepository.deleteById(id);

			logger.info("GradingController - deleteGradingByAdmin - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - deleteGradingByAdmin - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_TEACHER")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}/teacher")
	@JsonView(Views.Teacher.class)
	public ResponseEntity<?> deleteGradingByTeacher(@PathVariable Integer id, Principal principal) {
		logger.info("GradingController - deleteGradingByTeacher - starts.");
		try {
			GradingEntity grading = gradingRepository.findById(id).orElse(null);

			if (grading == null) {
				logger.info("GradingController - deleteGradingByTeacher - admin not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			AccountEntity account = accountRepository.findByUsername(principal.getName());

			TeacherEntity teacher = grading.getLecture().getTeacher();

			if (account.getRole().equals(EUserRole.ROLE_TEACHER))
				if (account.getUser().getUserId() != teacher.getUserId()) {
					
					logger.info("GradingController - deleteGradingByTeacher - you don't have permission to delete grading.");
					return new ResponseEntity<RESTError>(new RESTError("You don't have permission."),
							HttpStatus.FORBIDDEN);
				}

			gradingRepository.deleteById(id);

			logger.info("GradingController - deleteGradingByTeacher - finished.");
			return new ResponseEntity<GradingEntity>(grading, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("GradingController - deleteGradingByTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	@Secured({"ROLE_ADMIN", "ROLE_STUDENT"})
	@RequestMapping(method = RequestMethod.GET, value = "/by-student/{studentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findGradingsByStudentId(@PathVariable Integer studentId) {
		logger.info("GradingController - findGradingsByStudentId - starts.");
		try {
			StudentEntity student = studentRepository.findById(studentId).orElse(null);

			if (student == null) {
				logger.info("GradingController - findGradingsByStudentId - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<GradingEntity> gradings = new ArrayList();
				for(GradingEntity grading : student.getGradings())
					gradings.add(grading);
				
				logger.info("GradingController - findGradingsByStudentId - finished.");
				return new ResponseEntity<Iterable<GradingEntity>>(gradings, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("GradingController - findGradingsByStudentId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

