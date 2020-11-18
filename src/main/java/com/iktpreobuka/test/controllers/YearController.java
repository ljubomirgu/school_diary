package com.iktpreobuka.test.controllers;

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
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.entities.dto.YearDto;
import com.iktpreobuka.test.enumerations.EYear;
import com.iktpreobuka.test.repositories.ClassRepository;
import com.iktpreobuka.test.repositories.SubjectRepository;
import com.iktpreobuka.test.repositories.YearRepository;
import com.iktpreobuka.test.security.Views;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/years")
public class YearController {

	@Autowired
	private YearRepository yearRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private ClassRepository classRepository;

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllYears() {
		logger.info("YearController - getAllYears - starts.");

		return new ResponseEntity<Iterable<YearEntity>>(yearRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findYearById(@PathVariable Integer id) {
		logger.info("YearController - findYearById - starts.");
		try {
			YearEntity year = yearRepository.findById(id).orElse(null);

			if (year == null) {
				logger.info("YearController - findYearById - year not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			logger.info("YearController - findYearById - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("YearController - findYearById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewYear(@Valid @RequestBody YearDto newYear, BindingResult result) {
		logger.info("YearController - addNewYear - starts.");

		if (result.hasErrors()) {
			logger.info("YearController - addNewYear - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		if (newYear == null) {
			logger.info("YearController - addNewYear - year object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Year object is invalid."), HttpStatus.BAD_REQUEST);
		}

		if (newYear.getYear() == null) {
			logger.info("YearController - addNewYear - year object is invalid.");
			return new ResponseEntity<RESTError>(new RESTError("Year object is invalid."), HttpStatus.BAD_REQUEST);
		}

		if (!(newYear.getYear().equals(EYear.V.toString()) || newYear.getYear().equals(EYear.VI.toString())
				|| newYear.getYear().equals(EYear.VII.toString()) || newYear.getYear().equals(EYear.VIII.toString()))) {
			logger.info("YearController - addNewYear - wrong value of year.");
			return new ResponseEntity<RESTError>(new RESTError("Year object is invalid."), HttpStatus.BAD_REQUEST);
		}
		try {
			YearEntity year = new YearEntity();
//dodato:
			Iterable<YearEntity> years = yearRepository.findAll();
			for(YearEntity yearY : years) {
				if(yearY.getYear().equals(EYear.valueOf(newYear.getYear())))
					return new ResponseEntity<RESTError>(new RESTError("Year object olready exists."), HttpStatus.BAD_REQUEST);
			}
				
			year.setYear(EYear.valueOf(newYear.getYear()));
			yearRepository.save(year);

			logger.info("YearController - addNewYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		} 
		catch (Exception e) {	
			logger.info("YearController - addNewYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateYear(@Valid @RequestBody YearDto updateYear, BindingResult result, @PathVariable Integer id) {
		logger.info("YearController - updateYear - starts.");
		if (result.hasErrors()) {
			logger.info("YearController - updateYear - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
		try {
			YearEntity year = yearRepository.findById(id).orElse(null);

			if (year == null || updateYear == null) {
				logger.info("YearController - updateYear - starts.");
				return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
					HttpStatus.NOT_FOUND);
			}
		
			if (!(updateYear.getYear().equals(EYear.V.toString()) || updateYear.getYear().equals(EYear.VI.toString())
					|| updateYear.getYear().equals(EYear.VII.toString()) || updateYear.getYear().equals(EYear.VIII.toString()))) {
				logger.info("YearController - addNewYear - wrong value of year.");
				return new ResponseEntity<RESTError>(new RESTError("Year object is invalid."), HttpStatus.BAD_REQUEST);
			}
			
			Iterable<YearEntity> years = yearRepository.findAll();
			for(YearEntity yearr : years) {
				if(yearr.getYear().toString().equals(updateYear))
					return new ResponseEntity<RESTError>(new RESTError("Year object is invalid."), HttpStatus.BAD_REQUEST);
 
			}
			year.setYear(EYear.valueOf(updateYear.getYear()));

			yearRepository.save(year);
			logger.info("YearController - updateYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("YearController - updateYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

// dodavanje predmeta:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addSubjectToYear(@PathVariable Integer id, @PathVariable Integer subjectId) {
		logger.info("YearController - addSubjectToYear - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
			YearEntity year = yearRepository.findById(id).get();
			
			if (subject == null || year == null) {
				logger.info("YearController - addSubjectToYear - year and/or subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year or subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (year.getSubjects().contains(subject)) {
				logger.info("YearController - addSubjectToYear - that subject already appears in years list of subjects.");
				return new ResponseEntity<RESTError>(new RESTError("Year already have that subject."),
						HttpStatus.BAD_REQUEST);
				}

			year.getSubjects().add(subject);
			yearRepository.save(year);

			logger.info("YearController - addSubjectToYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("YearController - addSubjectToYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

// dodavanje odeljenja:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-class/{classtId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addClassToYear(@PathVariable Integer id, @PathVariable Integer classId) {
		logger.info("YearController - addClassToYear - starts.");
		try {
			ClassEntity schoolClass = classRepository.findById(classId).get();
			YearEntity year = yearRepository.findById(id).get();
			
			if (schoolClass == null || year == null) {
				logger.info("YearController - addClassToYear - starts.");
				return new ResponseEntity<RESTError>(new RESTError("Year or class with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (year.getClasses().contains(schoolClass)) {
				logger.info("YearController - addClassToYear - starts.");
				return new ResponseEntity<RESTError>(new RESTError("Year already have that class."),
						HttpStatus.BAD_REQUEST);
			}
			year.getClasses().add(schoolClass);
			yearRepository.save(year);
			
			logger.info("YearController - addClassToYear - starts.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("YearController - addClassToYear - starts.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteYear(@PathVariable Integer id) {
		logger.info("YearController - deleteYear - starts.");
		try {
			YearEntity year = yearRepository.findById(id).orElse(null);

			if (year == null) {
				logger.info("YearController - deleteYear - year not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			// provera da li neki predmet referencira taj razred:
			Iterable<SubjectEntity> subjects = subjectRepository.findAll();
			for (SubjectEntity subject : subjects) {
				if (subject.getYears().contains(year)) {
					logger.info("YearController - deleteYear - some subject has reference to this year so it cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("Year cannot be deleted because some subject has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			// provera da li neko odeljenje referencira taj razred:
			Iterable<ClassEntity> classes = classRepository.findAll();
			for (ClassEntity schoolClass : classes) {
				if (schoolClass.getYear().equals(year)) {
					logger.info("YearController - deleteYear - some class has reference to this year so it cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("Year cannot be deleted because some class has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			yearRepository.deleteById(id);

			logger.info("YearController - deleteYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
			
		}
		catch (Exception e) {
			logger.info("YearController - deleteYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	//front:
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/remove-subject/{subjectId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> removeSubjectToYear(@PathVariable Integer id, @PathVariable Integer subjectId) {
		logger.info("YearController - removeSubjectToYear - starts.");
		try {
			SubjectEntity subject = subjectRepository.findById(subjectId).orElse(null);
			YearEntity year = yearRepository.findById(id).get();
			
			if (subject == null || year == null) {
				logger.info("YearController - removeSubjectToYear - year and/or subject not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year or subject with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
/*			if (year.getSubjects().contains(subject)) {
				logger.info("YearController - removeSubjectToYear - that subject already appears in years list of subjects.");
				return new ResponseEntity<RESTError>(new RESTError("Year already have that subject."),
						HttpStatus.BAD_REQUEST);
				}
*/
			year.getSubjects().remove(subject);
			yearRepository.save(year);

			logger.info("YearController - removeSubjectToYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("YearController - removeSubjectToYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//CascadeType.All	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteYearCascade(@PathVariable Integer id) {
		logger.info("YearController - deleteYear - starts.");
		try {
			YearEntity year = yearRepository.findById(id).orElse(null);

			if (year == null) {
				logger.info("YearController - deleteYear - year not found.");
				return new ResponseEntity<RESTError>(new RESTError("Year with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}		

			yearRepository.deleteById(id);

			logger.info("YearController - deleteYear - finished.");
			return new ResponseEntity<YearEntity>(year, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.info("YearController - deleteYear - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
