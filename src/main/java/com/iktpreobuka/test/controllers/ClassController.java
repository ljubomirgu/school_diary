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
import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.entities.dto.ClassDto;
import com.iktpreobuka.test.repositories.ClassRepository;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;
import com.iktpreobuka.test.repositories.YearRepository;
import com.iktpreobuka.test.security.Views;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/classes")
public class ClassController {

	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private YearRepository yearRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private LectureRepository lectureRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllClasses() {
		logger.info("ClassController - getAllClasses - starts.");
		return new ResponseEntity<Iterable<ClassEntity>>(classRepository.findAll(), HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findClassById(@PathVariable Integer id) {
		logger.info("ClassController - findClassById - starts.");
		try {

			ClassEntity schoolClass = classRepository.findById(id).orElse(null);

			if (schoolClass == null) {
				logger.info("ClassController - findClassById - clas not found.");
				return new ResponseEntity<RESTError>(new RESTError("Class with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("ClassController - findClassById - finished.");
				return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ClassController - findClassById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewClass(@Valid @RequestBody ClassDto newClass,  BindingResult result) {
		logger.info("ClassController - addNewClass - starts.");
		try {
			if (result.hasErrors()) {
				logger.info("ClassController - addNewClass - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			if (newClass == null) {
				logger.info("ClassController - addNewClass - class object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Class object is invalid."), HttpStatus.BAD_REQUEST);
			}
			if (newClass.getNumberOfDepartment() == null) {
				logger.info("ClassController - addNewClass - number of department is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Number of department is invalid."),
						HttpStatus.BAD_REQUEST);
			}
			if (newClass.getSchoolYear() == null) {
				logger.info("ClassController - addNewClass - school year is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("School year is invalid."), HttpStatus.BAD_REQUEST);
			}

			if (newClass.getYearId() == null) {
				logger.info("ClassController - addNewClass - year is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Year  is invalid."), HttpStatus.BAD_REQUEST);
			}

			YearEntity year = yearRepository.findById(newClass.getYearId()).orElse(null);
			if (year == null) {
				logger.info("ClassController - addNewClass - year is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Year  is invalid."), HttpStatus.BAD_REQUEST);
			}

			ClassEntity schoolClasss = classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(year,
					newClass.getNumberOfDepartment(), newClass.getSchoolYear());
			
			if (schoolClasss != null) {
				logger.info("ClassController - addNewClass - class already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Class already exists."), HttpStatus.BAD_REQUEST);
			}
			else {

				ClassEntity schoolClass = new ClassEntity();
				schoolClass.setNumberOfDepartment(newClass.getNumberOfDepartment());
				schoolClass.setSchoolYear(newClass.getSchoolYear());
				schoolClass.setYear(year);
				classRepository.save(schoolClass);

				logger.info("ClassController - addNewClass - finished.");
				return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ClassController - addNewClass - internal server error.");
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
	public ResponseEntity<?> updateClass(@PathVariable Integer id, @Valid @RequestBody ClassDto updateClass,BindingResult result ) {
		logger.info("ClassController - updateClass - starts.");
		try {
			if (result.hasErrors()) {
				logger.info("ClassController - updateClass - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			if(updateClass == null) {
				logger.info("ClassController - updateClass - not found class.");
				return new ResponseEntity<RESTError>(new RESTError("Class object is invalid"),
					HttpStatus.BAD_REQUEST);
			}
			
			if(updateClass.getNumberOfDepartment()==null && updateClass.getSchoolYear()==null && updateClass.getYearId()==null) {
				logger.info("ClassController - updateClass - update year is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid."), HttpStatus.BAD_REQUEST);
			}
			
			ClassEntity schoolClass = classRepository.findById(id).orElse(null);
			
			if (schoolClass == null)  {
				logger.info("ClassController - updateClass - not found class.");
				return new ResponseEntity<RESTError>(new RESTError("Class with provided ID not found."),
					HttpStatus.NOT_FOUND);
			}
			
			if(updateClass.getNumberOfDepartment()!=null && updateClass.getSchoolYear()==null && updateClass.getYearId()==null) {
				
				if(classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(schoolClass.getYear(),
						 updateClass.getNumberOfDepartment(),schoolClass.getSchoolYear())!=null);
				
				logger.info("ClassController - updateClass - that department already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
			}
			
			if(updateClass.getNumberOfDepartment()==null && updateClass.getSchoolYear()!=null && updateClass.getYearId()==null) {
				
				if(classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(schoolClass.getYear(),
						 schoolClass.getNumberOfDepartment(),updateClass.getSchoolYear())!=null) {				
							logger.info("ClassController - updateClass - that department already exists.");
							return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
				}
			}
			
			if (updateClass.getNumberOfDepartment() == null && updateClass.getSchoolYear() == null	&& updateClass.getYearId() != null) {
				YearEntity year = yearRepository.findById(updateClass.getYearId()).orElse(null);
				if(year==null)
					return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);

				if (classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(year,
						schoolClass.getNumberOfDepartment(), schoolClass.getSchoolYear()) != null) {
				logger.info("ClassController - updateClass - that department already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
				}
			}
		
			if (updateClass.getNumberOfDepartment() == null && updateClass.getSchoolYear() != null	&& updateClass.getYearId() != null) {
				YearEntity year = yearRepository.findById(updateClass.getYearId()).orElse(null);
				if(year==null)
					return new ResponseEntity<RESTError>(new RESTError("Update year  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);

				if (classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(year,
						schoolClass.getNumberOfDepartment(), updateClass.getSchoolYear()) != null) {
				logger.info("ClassController - updateClass - that department already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
				}
			}
			
			if (updateClass.getNumberOfDepartment() != null && updateClass.getSchoolYear() == null	&& updateClass.getYearId() != null) {
				YearEntity year = yearRepository.findById(updateClass.getYearId()).orElse(null);
				if(year==null)
					return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);

				if (classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(year,
						updateClass.getNumberOfDepartment(), schoolClass.getSchoolYear()) != null) {
				logger.info("ClassController - updateClass - that department already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
				}
			}
			
			if (updateClass.getNumberOfDepartment() != null && updateClass.getSchoolYear() != null	&& updateClass.getYearId() == null) {	
				if (classRepository.findByYearAndNumberOfDepartmentAndSchoolYear(schoolClass.getYear(),
						updateClass.getNumberOfDepartment(), updateClass.getSchoolYear()) != null) {
				logger.info("ClassController - updateClass - that department already exists.");
				return new ResponseEntity<RESTError>(new RESTError("Update class  is invalid.That class already exists."), HttpStatus.BAD_REQUEST);
				}
			}
			
			if (updateClass.getNumberOfDepartment() != null && updateClass.getSchoolYear() != null	&& updateClass.getYearId() != null
					&& updateClass.getNumberOfDepartment() != schoolClass.getNumberOfDepartment() && updateClass.getSchoolYear()!=schoolClass.getSchoolYear()
					&& schoolClass.getYear().getYearId()!=updateClass.getYearId()) {
				logger.info("ClassController - updateClass - you need create new class.");
				return new ResponseEntity<RESTError>(new RESTError("You need to create new class, not update some which exists."), HttpStatus.BAD_REQUEST);

			}

			if (StringUtils.isNotBlank(updateClass.getNumberOfDepartment())) {
				schoolClass.setNumberOfDepartment(updateClass.getNumberOfDepartment());
			}
			if (StringUtils.isNotBlank(updateClass.getSchoolYear())) {
				schoolClass.setSchoolYear(updateClass.getSchoolYear());
			}
			YearEntity year = yearRepository.findById(updateClass.getYearId()).orElse(null);
			if(year!= null)
				schoolClass.setYear(year);

			logger.info("ClassController - updateClass - finished.");
			return new ResponseEntity<ClassEntity>(classRepository.save(schoolClass), HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("ClassController - updateClass - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-student/{studentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addStudentToYear(@PathVariable Integer id, @PathVariable Integer studentId) {
		logger.info("ClassController - addStudentToYear - starts.");
		try {
			StudentEntity student = studentRepository.findById(studentId).orElse(null);
			ClassEntity schoolClass = classRepository.findById(id).orElse(null);
			
			if (student == null || schoolClass == null) {
				logger.info("ClassController - addStudentToYear - Class or student with provided ID not found.");
				return new ResponseEntity<RESTError>(new RESTError("Class or student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (schoolClass.getStudents().contains(student)) {
				logger.info("ClassController - addStudentToYear -  subject already appears in the list of subjects.");
				return new ResponseEntity<RESTError>(
						new RESTError("The subject already appears in the list of subjects."), HttpStatus.BAD_REQUEST);
			}
			schoolClass.getStudents().add(student);
			classRepository.save(schoolClass);

			logger.info("ClassController - addStudentToYear - finished.");
			return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("ClassController - addStudentToYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-lecture/{lectureId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addLectureToYear(@PathVariable Integer id, @PathVariable Integer lectureId) {
		logger.info("ClassController - addLectureToYear - starts.");
		try {
			LectureEntity lecture = lectureRepository.findById(lectureId).orElse(null);
			ClassEntity schoolClass = classRepository.findById(id).orElse(null);
			
			if (lecture == null || schoolClass == null) {
				logger.info("ClassController - addLectureToYear - class or lecture with provided ID not found.");
				return new ResponseEntity<RESTError>(new RESTError("Class or lecture with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (schoolClass.getLectures().contains(lecture)) {
				logger.info("ClassController - addLectureToYear - lecture already appears in the list of lectures.");
				return new ResponseEntity<RESTError>(
						new RESTError("The lecture already appears in the list of lectures."), HttpStatus.BAD_REQUEST);
			}

			schoolClass.getLectures().add(lecture);
			classRepository.save(schoolClass);

			logger.info("ClassController - addLectureToYear - finished.");
			return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("ClassController - addLectureToYear - starts.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteClass(@PathVariable Integer id) {
		logger.info("ClassController - deleteClass - starts.");
		try {
			ClassEntity schoolClass = classRepository.findById(id).orElse(null);

			if (schoolClass == null) {
				logger.info("ClassController - deleteClass - clas not found.");
				return new ResponseEntity<RESTError>(new RESTError("Class with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			Iterable<StudentEntity> students = studentRepository.findAll();
			for (StudentEntity student : students)
				if (student.getSchoolClass().equals(schoolClass)) {
					logger.info("ClassController - deleteClass - there is a student who goes to that class.");
					return new ResponseEntity<RESTError>(new RESTError("The class cannot be deleted."),
							HttpStatus.BAD_REQUEST);
				}
			
			List<LectureEntity> lectures = (List<LectureEntity>) lectureRepository.findAll();
			for (LectureEntity lecture : lectures)
				if (lecture.getSchoolClass().equals(schoolClass)) {
					logger.info("ClassController - deleteClass - class cannot be deleted, some lecture has references to it.");
					return new ResponseEntity<RESTError>(new RESTError("The class cannot be deleted."),
							HttpStatus.BAD_REQUEST);
				}
			classRepository.deleteById(id);

			logger.info("ClassController - deleteClass - finished.");
			return new ResponseEntity<ClassEntity>(schoolClass, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("ClassController - deleteClass - starts.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by-year/{yearId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findSchoolClassesByYearId(@PathVariable Integer yearId) {
		logger.info("ClassController - findSchoolClassesByYearId - starts.");
		try {
			YearEntity year = yearRepository.findById(yearId).orElse(null);

			if (year == null) {
				logger.info("ClassController - findSchoolClassesByYearId - year not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<ClassEntity> schoolClasses = new ArrayList();
				for(ClassEntity schoolClass : year.getClasses())
					schoolClasses.add(schoolClass);
				
				logger.info("ClassController - findSchoolClassesByYearId - finished.");
				return new ResponseEntity<Iterable<ClassEntity>>(schoolClasses, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ClassController - findSchoolClassesByYearId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by-subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findClassesBySubjectId(@PathVariable Integer subjectId) {
		logger.info("ClassController - findClassesBySubjectId - starts.");
		try {
			
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);

			if (subject == null) {
				logger.info("ClassController - findClassesBySubjectId - subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				Iterable<ClassEntity> classes = new ArrayList<>();
				((ArrayList<ClassEntity>) classes).addAll(classRepository.findAllBySubjectId(subjectId));
						
				logger.info("ClassController - findClassById - finished.");
				return new ResponseEntity<Iterable<ClassEntity>>(classes, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ClassController - findClassById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/by-teacher/{teacherId}/by-subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findClassesBySubjectIdAndTeacherId(@PathVariable Integer subjectId, Integer teacherId) {
		logger.info("ClassController - findClassesBySubjectIdAndTeacherId - starts.");
		try {
			
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
			TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);

			if (subject == null || teacher == null) {
				logger.info("ClassController - findClassesBySubjectIdAndTeacherId - subject or teacher not found.");
				return new ResponseEntity<RESTError>(new RESTError("subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} 			
			
			
			else {
				
				Iterable<ClassEntity> classes = new ArrayList<>();
				((ArrayList<ClassEntity>) classes).addAll(classRepository.findAllBySubjectIdAndTeacherId(subjectId, teacherId));
						
				logger.info("ClassController - findClassesBySubjectIdAndTeacherId - finished.");
				return new ResponseEntity<Iterable<ClassEntity>>(classes, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ClassController - findClassesBySubjectIdAndTeacherId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
	
}
