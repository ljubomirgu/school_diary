package com.iktpreobuka.test.controllers;

import java.util.ArrayList;
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
import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.dto.LectureDto;
import com.iktpreobuka.test.repositories.ClassRepository;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;
import com.iktpreobuka.test.security.Views;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/lectures")
public class LectureController {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private ClassRepository classRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());


	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllLectures() {
		logger.info("LectureController - getAllLectures - starts.");

		return new ResponseEntity<Iterable<LectureEntity>>(lectureRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findLectureById(@PathVariable Integer id) {
		logger.info("LectureController - findLectureById - starts.");
		try {
			LectureEntity lecture = lectureRepository.findById(id).orElse(null);

			if (lecture == null) {
				logger.info("LectureController - findLectureById - lecture not found.");
				return new ResponseEntity<RESTError>(new RESTError("Lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {

				logger.info("LectureController - findLectureById - finished.");
				return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
			}
		}
		catch (Exception e) {
			logger.info("LectureController - findLectureById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addLecture(@Valid @RequestBody LectureDto newLecture, BindingResult result) {
		logger.info("LectureController - addLecture - starts.");

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		if (newLecture == null) {
			return new ResponseEntity<RESTError>(new RESTError("Lecture object isS invalid."), HttpStatus.BAD_REQUEST);
		}

		if(newLecture.getClassId()==null || newLecture.getSubjectId()==null || newLecture.getTeacherId()==null) {
			logger.info("LectureController - addLecture - some of atributs are null.");
			return new ResponseEntity<RESTError>(new RESTError("Lecture object isSS invalid."), HttpStatus.BAD_REQUEST);
		}
		try {
			ClassEntity schoolClass = classRepository.findById(newLecture.getClassId()).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(newLecture.getTeacherId()).orElse(null);
			SubjectEntity subject = subjectRepository.findById(newLecture.getSubjectId()).orElse(null);

			if (schoolClass == null || teacher == null || subject == null) {
				logger.info("LectureController - addLecture - lecture object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."),
						HttpStatus.BAD_REQUEST);
			}
		
			LectureEntity lecturee = lectureRepository.findBySchoolClassAndSubjectAndTeacher(schoolClass, subject,teacher);
			if(lecturee != null) {
				logger.info("LectureController - addLecture - lecture already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Lecture already exists."), HttpStatus.BAD_REQUEST);
			}

			if (teacher.getSubjects().contains(subject)) {
				if (subject.getYears().contains(schoolClass.getYear())) {

					LectureEntity lecture = new LectureEntity();
					lecture.setSchoolClass(schoolClass);
					lecture.setSubject(subject);
					lecture.setTeacher(teacher);
					lectureRepository.save(lecture);
				
					logger.info("LectureController - addLecture - finished.");
					return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
				}
				else {
					logger.info("LectureController - addLecture - the subject is not taught in that class.");
					return new ResponseEntity<RESTError>(new RESTError("That is not subjects' year."), HttpStatus.BAD_REQUEST);
				}
			}
			else {
				logger.info("LectureController - addLecture - the teacher does not teach the subject.");
				return new ResponseEntity<RESTError>(new RESTError("That is not teachers' subject."), HttpStatus.BAD_REQUEST);
			}

		}
		catch (Exception e){
			logger.info("LectureController - addLecture - internal server error.");
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
	public ResponseEntity<?> updateLecture(@Valid @RequestBody LectureDto updateLecture,  BindingResult result, @PathVariable Integer id) {
		logger.info("LectureController - updateLecture - starts.");
		try {
			if (result.hasErrors()) {
				logger.info("LectureController - updateLecture - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			LectureEntity lecture = lectureRepository.findById(id).get();
	
			if (lecture == null || updateLecture == null) {
				return new ResponseEntity<RESTError>(new RESTError("Lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
		
			if(updateLecture.getClassId()==null && updateLecture.getTeacherId()==null && updateLecture.getSubjectId()==null)
				return new ResponseEntity<RESTError>(new RESTError("Update lecture object is invalid."), HttpStatus.BAD_REQUEST);

			ClassEntity schoolClass = new ClassEntity();
			if(updateLecture.getClassId()==null)
				schoolClass = null;
			else
				schoolClass = classRepository.findById(updateLecture.getClassId()).orElse(null);
		
			TeacherEntity teacher = new TeacherEntity();
			if(updateLecture.getTeacherId()==null)
				teacher=null;	
			else
				teacher = teacherRepository.findById(updateLecture.getTeacherId()).orElse(null);
		
			SubjectEntity subject = new SubjectEntity();
			if(updateLecture.getSubjectId()==null)
				subject=null;	
			else
				subject = subjectRepository.findById(updateLecture.getSubjectId()).orElse(null);
				
			ClassEntity oldSchoolClass = lecture.getSchoolClass();
			TeacherEntity oldTeacher = lecture.getTeacher();
			SubjectEntity oldSubject = lecture.getSubject();

		
			if(teacher != null && schoolClass == null && subject == null)
				if(teacher.getSubjects().contains(oldSubject)) {
						lecture.setTeacher(teacher);
						lectureRepository.save(lecture);
						logger.info("LectureController - updateLecture 1- Lecture object is invalid.");
						return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
			}else 
				return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		
		
			if(teacher == null && schoolClass != null && subject == null)
				if(schoolClass.getYear().getSubjects().contains(oldSubject)) {
					lecture.setSchoolClass(schoolClass);
					lectureRepository.save(lecture);
					logger.info("LectureController - updateLecture 2- Lecture object is invalid.");
					return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
				}else 
					return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
			
		
			if(teacher == null && schoolClass == null && subject != null)
				if(oldTeacher.getSubjects().contains(subject)) {
					if(oldSchoolClass.getYear().getSubjects().contains(subject)){
						lecture.setSubject(subject);
						lectureRepository.save(lecture);
						logger.info("LectureController - updateLecture 3- Lecture object is invalid.");
						return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
					}else 
						return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
				}else 
					return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);	
		
		
		
			if(teacher != null && schoolClass != null && subject == null)
				if(teacher.getSubjects().contains(oldSubject)) {
					if(schoolClass.getYear().getSubjects().contains(oldSubject)) {
						lecture.setTeacher(teacher);
						lecture.setSchoolClass(schoolClass);
						lectureRepository.save(lecture);
						logger.info("LectureController - updateLecture 4- Lecture object is invalid.");
						return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);		
					}else 
						return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
				}else 
					return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		
		
		
			if(teacher != null && schoolClass == null && subject != null)
				if(teacher.getSubjects().contains(subject)) {
					if(oldSchoolClass.getYear().getSubjects().contains(subject)) {
						lecture.setTeacher(teacher);
						lecture.setSubject(subject);
						lectureRepository.save(lecture);
						logger.info("LectureController - updateLecture 5- Lecture object is invalid.");
						return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);		
					}else 
						return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
				}else 
					return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
		
		
			if(teacher == null && schoolClass != null && subject != null)
				if(oldTeacher.getSubjects().contains(subject)) {
					if(schoolClass.getYear().getSubjects().contains(subject)) {
						lecture.setSubject(subject);
						lecture.setSchoolClass(schoolClass);
						lectureRepository.save(lecture);
						logger.info("LectureController - updateLecture 6- Lecture object is invalid.");
						return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);		
					}else 
						return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);
				}else 
					return new ResponseEntity<RESTError>(new RESTError("Lecture object is invalid."), HttpStatus.BAD_REQUEST);

		
			logger.info("LectureController - updateLecture - finished.");
			return new ResponseEntity<RESTError>(new RESTError("You need to add a new lecture, not modify an existing one."), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e){
			logger.info("LectureController - updateLecture - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteLecture(@PathVariable Integer id) {
		logger.info("LectureController - deleteLecture - starts.");
		try {
			LectureEntity lecture = lectureRepository.findById(id).get();
			
			if (lecture == null) {
				return new ResponseEntity<RESTError>(new RESTError("Lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
				
			Iterable<TeacherEntity> teachers = teacherRepository.findAll();
			for (TeacherEntity teacher : teachers) {
				if (teacher.getLectures().contains(lecture))
					return new ResponseEntity<RESTError>(new RESTError("Lecture cannot be deleted because some teacher has a reference to it"),
							HttpStatus.BAD_REQUEST);
			}
				
			Iterable<SubjectEntity> subjects = subjectRepository.findAll();
			for (SubjectEntity subject : subjects) {
				if (subject.getLectures().contains(lecture))
					return new ResponseEntity<RESTError>(new RESTError("Lecture cannot be deleted because some subject has a reference to it"),
							HttpStatus.BAD_REQUEST);		
			}
		
			Iterable<ClassEntity> classes = classRepository.findAll();
			for(ClassEntity schoolClass : classes) {
				if(schoolClass.getLectures().contains(lecture))
					return new ResponseEntity<RESTError>(new RESTError("Lecture cannot be deleted because some class has a reference to it"),
							HttpStatus.BAD_REQUEST);
			}	
			
			lectureRepository.deleteById(id);
				
			logger.info("LectureController - deleteLecture - finished.");
			return new ResponseEntity<LectureEntity>(lecture, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("LectureController - deleteLecture - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}	

	@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/by-teacher/{teacherId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findLecturesByTeacherId(@PathVariable Integer teacherId) {
		logger.info("LectureController - findLectureById - starts.");
		try {
			TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);
			List<LectureEntity> lectures = lectureRepository.findAllByTeacher(teacher);

			if (lectures == null) {
				logger.info("LectureController - findLecturesByTeacherId - lectures not found.");
				return new ResponseEntity<RESTError>(new RESTError("Lectures not found."),
						HttpStatus.NOT_FOUND);
			} else {

				logger.info("LectureController - findLecturesByTeacherId - finished.");
				return new ResponseEntity<List<LectureEntity>>(lectures, HttpStatus.OK);
			}
		}
		catch (Exception e) {
			logger.info("LectureController - findLectureById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by-department/{schoolClassId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findLecturesByDepartmentId(@PathVariable Integer schoolClassId) {
		logger.info("LectureController - findLectureById - starts.");
		try {
			ClassEntity depart = classRepository.findById(schoolClassId).orElse(null);
			List<LectureEntity> lectures = lectureRepository.findAllBySchoolClass(depart);

			if (lectures == null) {
				logger.info("LectureController - findLecturesByTeacherId - lectures not found.");
				return new ResponseEntity<RESTError>(new RESTError("Lectures not found."),
						HttpStatus.NOT_FOUND);
			} else {

				logger.info("LectureController - findLecturesByTeacherId - finished.");
				return new ResponseEntity<List<LectureEntity>>(lectures, HttpStatus.OK);
			}
		}
		catch (Exception e) {
			logger.info("LectureController - findLectureById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/teacher/{teacherId}/subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findLecturesByTeacherIdAndSubjectId(@PathVariable Integer teacherId, Integer subjectId) {
		logger.info("LectureController - findLectureById - starts.");
		try {
			TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
			List<LectureEntity> lectures = lectureRepository.findAllByTeacher(teacher);

			if (lectures == null) {
				logger.info("LectureController - findLecturesByTeacherIdAndSubjectId - lectures not found.");
				return new ResponseEntity<RESTError>(new RESTError("Lectures not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				List<LectureEntity> lects = new ArrayList<>();
				for(LectureEntity lecture : lectures) {
					if(lecture.getSubject().getSubjectId()==subjectId) {
						lects.add(lecture);
					}
				}
				
				if(lects.isEmpty())
				{
					logger.info("LectureController - findLecturesByTeacherIdAndSubjectId - lects not found.");
					return new ResponseEntity<RESTError>(new RESTError("Lects not found."),
							HttpStatus.NOT_FOUND);
				}else {

						logger.info("LectureController - findLecturesByTeacherIdAndSubjectId - finished.");
						return new ResponseEntity<List<LectureEntity>>(lects, HttpStatus.OK);
					}
		}}
		catch (Exception e) {
			logger.info("LectureController - findLecturesByTeacherIdAndSubjectId - internal server errorrrrr.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
