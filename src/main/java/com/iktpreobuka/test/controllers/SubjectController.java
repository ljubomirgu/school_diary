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
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.entities.dto.SubjectDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;
import com.iktpreobuka.test.repositories.YearRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.SubjectDao;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(value = "diary/subjects")
public class SubjectController {
	
	@Autowired
	private SubjectRepository subjectRepository;	
	@Autowired
	private YearRepository yearRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private LectureRepository lectureRepository;
	
	@Autowired
	private SubjectDao subjectDao;
	

	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllSubjectsAdmin() {
		logger.info("SubjectController - getAllSubjectsAdmin - starts.");

		return new ResponseEntity<Iterable<SubjectEntity>>(subjectRepository.findAll(), HttpStatus.OK);
	}
	
/*	
	@RequestMapping(method = RequestMethod.GET ,value="/review-teachers'")
	public ResponseEntity<List<SubjectEntity>> reviewTeachersSubjects(@RequestParam Integer teacherId,
			@RequestParam Boolean fromTheCurrentSchoolAge) {
		if (teacherId == null || fromTheCurrentSchoolAge == null) {
			return new ResponseEntity("All fields are mandatory.", HttpStatus.BAD_REQUEST);
		}
		//return subjectDao.findTeachersSubjects(teacherId, fromTheCurrentSchoolAge);
		return new ResponseEntity<List<SubjectEntity>>(subjectDao.findTeachersSubjects(teacherId, fromTheCurrentSchoolAge), HttpStatus.OK);
	}
	*/

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findSubjectById(@PathVariable Integer id) {
		logger.info("SubjectController - findSubjectById - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);

			if (subject == null) {
				logger.info("SubjectController - findSubjectById - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("SubjectController - findSubjectById - finished.");
				return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("SubjectController - findSubjectById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewSubject(@Valid @RequestBody SubjectDto newSubject, BindingResult result) {
		logger.info("SubjectController - addNewSubject - starts.");

		if (result.hasErrors()) {
			logger.info("SubjectController - addNewSubject - validate error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		if (newSubject == null) {
			logger.info("SubjectController - addNewSubject - subject not found.");
			return new ResponseEntity<RESTError>(new RESTError("Subject object is invalid."), HttpStatus.BAD_REQUEST);
		}

		if (newSubject.getSubjectName() == null || newSubject.getWeeklyFund() == null) {
			logger.info("SubjectController - addNewSubject - subject object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Subject object is invalid."), HttpStatus.BAD_REQUEST);
		}
		try {
			SubjectEntity subjectOld = subjectRepository.findBySubjectName(newSubject.getSubjectName());
			if (subjectOld != null) {
				logger.info("SubjectController - addNewSubject - subject already exists.");
				return new ResponseEntity<RESTError>(
						new RESTError("Subject whit name " + newSubject.getSubjectName() + " already exists"),
						HttpStatus.BAD_REQUEST);
			}
			SubjectEntity subject = new SubjectEntity();
			subject.setSubjectName(newSubject.getSubjectName());
			subject.setWeeklyFund(newSubject.getWeeklyFund());

			subjectRepository.save(subject);

			logger.info("SubjectController - addNewSubject - finished.");
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("SubjectController - addNewSubject - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateSubject(@Valid @RequestBody SubjectDto updateSubject, BindingResult result,
			@PathVariable Integer id) {

		logger.info("SubjectController - updateSubject - starts.");
		if (result.hasErrors()) {
			logger.info("SubjectController - updateSubject - validate error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);

			if (subject == null || updateSubject == null) {
				logger.info("SubjectController - updateSubject - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if (StringUtils.isNotBlank(updateSubject.getSubjectName())) {
				subject.setSubjectName(updateSubject.getSubjectName());
			}
			if (updateSubject.getWeeklyFund() != 0) {
				subject.setWeeklyFund(updateSubject.getWeeklyFund());
			}
			subjectRepository.save(subject);

			logger.info("SubjectController - updateSubject - finished.");
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("SubjectController - updateSubject - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// dodavanje razreda:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-year/{yearId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addYearToSubject(@PathVariable Integer id, @PathVariable Integer yearId) {
		logger.info("SubjectController - addYearToSubject - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);
			YearEntity year = yearRepository.findById(yearId).orElse(null);
			if (subject == null || year == null) {
				logger.info("SubjectController - addYearToSubject - subject or year not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject or year with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (subject.getYears().contains(year)) {
				logger.info("SubjectController - addYearToSubject - subject already has subject.");
				return new ResponseEntity<RESTError>(new RESTError("Subject already has that year."),
						HttpStatus.BAD_REQUEST);
			}
			subject.getYears().add(year);
			subjectRepository.save(subject);

			logger.info("SubjectController - addYearToSubject - finished.");
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("SubjectController - addYearToSubject - internal server errors.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	// dodavanje nastavnika:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-teacher/{teacherId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addTeacherToSubject(@PathVariable Integer id, @PathVariable Integer teacherId) {
		logger.info("SubjectController - addTeacherToSubject - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);
			if (subject == null || teacher == null) {
				logger.info("SubjectController - addTeacherToSubject - subject and/or teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject or teacher with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (subject.getTeachers().contains(teacher)) {
				logger.info("SubjectController - addTeacherToSubject - the subject is already being taught by that teacher.");
				return new ResponseEntity<RESTError>(
						new RESTError("The teacher already exists in the list of teachers who teach the subject."),
						HttpStatus.BAD_REQUEST);
			}
			subject.getTeachers().add(teacher);
			subjectRepository.save(subject);

			logger.info("SubjectController - addTeacherToSubject - finished.");
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.info("SubjectController - addTeacherToSubject - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//DA LI JE OK I IMA LI SMISLA AKO IMAM KONTROLER LECTURE?????:
	// dodavanje predaje:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-lecture/{lectureId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addLectureToSubject(@PathVariable Integer id, @PathVariable Integer lectureId) {
		logger.info("SubjectController - addLectureToTeacher - starts.");
		try {
			LectureEntity lecture = lectureRepository.findById(lectureId).orElse(null);
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);

			if (lecture == null || subject == null) {
				logger.info("SubjectController - addLectureToTeacher - subject and/or lecture not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject or lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (subject != lecture.getSubject()) {
				logger.info("SubjectController - addLectureToTeacher - subject object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError(
						"Lecture object is invalid. The subject and subject from the lecture are not same."),
						HttpStatus.BAD_REQUEST);
			}

// višak? misli da nije:
			if (subject.getLectures().contains(lecture)) {
				logger.info("SubjectController - addLectureToTeacher - subject has that lecture.");
				return new ResponseEntity<RESTError>(
						new RESTError("The lecture already appears in the list of lectures."), HttpStatus.BAD_REQUEST);
			}
			if (subject.getTeachers().contains(lecture.getTeacher())) {
				if (subject.getYears().contains(lecture.getSchoolClass().getYear())) {
					subject.getLectures().add(lecture);
					subjectRepository.save(subject);

					logger.info("SubjectController - addLectureToTeacher - finished.");
					return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
				}
			}
			logger.info("SubjectController - addLectureToTeacher - lecture object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		} 
		catch (Exception e) {
			logger.info("SubjectController - addLectureToTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

/*
// dodavanje predaje preko path - svodi sa na pravljene novog Predaje objekta:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-teacher/{teacherId}/add-class/{classId}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addLectureToSubjectByThreeIds(@PathVariable Integer id, @PathVariable Integer teacherId,@PathVariable Integer classId) {
		logger.info("SubjectController - addLectureToTeacher - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);
			
			if (lecture == null || subject == null) {
				logger.info("SubjectController - addLectureToTeacher - subject and/or lecture not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject or lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (subject != lecture.getSubject()) {
				logger.info("SubjectController - addLectureToTeacher - subject object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError(
						"Lecture object is invalid. The subject and subject from the lecture are not same."),
						HttpStatus.BAD_REQUEST);
			}

//višak? misli da nije:
			if (subject.getLectures().contains(lecture)) {
				logger.info("SubjectController - addLectureToTeacher - subject has that lecture.");
				return new ResponseEntity<RESTError>(
						new RESTError("The lecture already appears in the list of lectures."), HttpStatus.BAD_REQUEST);
			}
			if (subject.getTeachers().contains(lecture.getTeacher())) {
				if (subject.getYears().contains(lecture.getSchoolClass().getYear())) {
					subject.getLectures().add(lecture);
					subjectRepository.save(subject);

					logger.info("SubjectController - addLectureToTeacher - finished.");
					return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
				}
			}
			logger.info("SubjectController - addLectureToTeacher - lecture object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		} 
		catch (Exception e) {
			logger.info("SubjectController - addLectureToTeacher - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	*/
	
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteSubject(@PathVariable Integer id) {
		logger.info("SubjectController - deleteSubject - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(id).orElse(null);

			if (subject == null) {
				logger.info("SubjectController - deleteSubject - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			// provera da li neko predavanje referencira taj predmet:
			Iterable<LectureEntity> lectures = lectureRepository.findAll();
			for (LectureEntity lecture : lectures) {
				if (lecture.getSubject().equals(subject)) {
					logger.info(
							"SubjectController - deleteSubject - someone or something references subject and it cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("Subject cannot be deleted because some lecture has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}
			// provera da li neki razred referencira taj predmet:
			Iterable<YearEntity> years = yearRepository.findAll();
			for (YearEntity year : years) {
				if (year.getSubjects().contains(subject)) {
					logger.info(
							"SubjectController - deleteSubject -  someone or something references subject and it cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("Subject cannot be deleted because some year has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			// provera da li neko od nastavnika referencira taj predmet:
			Iterable<TeacherEntity> teachers = teacherRepository.findAll();
			for (TeacherEntity teacher : teachers) {
				if (teacher.getSubjects().contains(subject)) {
					logger.info(
							"SubjectController - deleteSubject -  someone or something references subject and it cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("Subject cannot be deleted because some teacher has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			/*
			 * pre brisanja predmeta treba ga obrisati iz svih tabela gde se pojavljuje,
			 * kako to? da li je ok CascadeType.DELETE? 
			 * subject.getLectures().clear();
			 * subject.getTeachers().clear();
			 *  subject.getYears().clear();
			 */
			subjectRepository.deleteById(id);

			logger.info("SubjectController - deleteSubject - finished.");
			return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("SubjectController - deleteSubject - internal server error"
					+ ".");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	
//front:
	
	//svi predmeti jednog profesora	
		@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
		@RequestMapping(method = RequestMethod.GET, value = "/teacher/{teacherId}")
//		@JsonView(Views.Admin.class)
		public ResponseEntity<?> findSubjecstByTeacherId(@PathVariable Integer teacherId) {
			logger.info("SubjectController - findSubjecstByTeacherId - starts.");
			try {
				TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);

				if (teacher == null) {
					logger.info("SubjectController - findSubjecstByTeacherId - teacher not found.");
					return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found."),
							HttpStatus.NOT_FOUND);
				} else {
					
					ArrayList<SubjectEntity> subjects = new ArrayList();
					for(SubjectEntity subject : teacher.getSubjects())
						subjects.add(subject);
					
					logger.info("SubjectController - findSubjecstByTeacherId - finished.");
					return new ResponseEntity<Iterable<SubjectEntity>>(subjects, HttpStatus.OK);
				}
			} catch (Exception e) {
				logger.info("SubjectController - findSubjecstByTeacherId - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		@Secured("ROLE_ADMIN")
		@RequestMapping(method = RequestMethod.GET, value = "/by-year/{yearId}")
//		@JsonView(Views.Admin.class)
		public ResponseEntity<?> findSubjecstByYearId(@PathVariable Integer yearId) {
			logger.info("SubjectController - findSubjecstByYearId - starts.");
			try {
				YearEntity year = yearRepository.findById(yearId).orElse(null);

				if (year == null) {
					logger.info("SubjectController - findSubjecstByYearId - year not found.");
					return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
							HttpStatus.NOT_FOUND);
				} else {
					
					ArrayList<SubjectEntity> subjects = new ArrayList(); // bolje je subjects=year.getSubjects()
					for(SubjectEntity subject : year.getSubjects())
						subjects.add(subject);
					
					logger.info("SubjectController - findSubjecstByYearId - finished.");
					return new ResponseEntity<Iterable<SubjectEntity>>(subjects, HttpStatus.OK);
				}
			} catch (Exception e) {
				logger.info("SubjectController - findSubjecstByYearId - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//za CascadeTyp=All:	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateStudent(@PathVariable Integer id) {
		logger.info("StudentController - deactivateStudent - starts.");

		SubjectEntity subject = subjectRepository.findById(id).orElse(null);

		if (subject == null) {
			return new ResponseEntity<RESTError>(new RESTError("Subject with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}

		subjectRepository.delete(subject);
		
		logger.info("StudentController - deactivateStudent - finished.");
		return new ResponseEntity<SubjectEntity>(subject, HttpStatus.OK);
	}
}
