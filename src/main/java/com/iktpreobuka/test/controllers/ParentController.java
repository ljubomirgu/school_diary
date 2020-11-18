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
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.ParentDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.repositories.ParentRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.AccountDao;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/parents")
public class ParentController {
	
	@Autowired
	private ParentRepository parentRepository;	
	@Autowired
	private StudentRepository studentRepository;	
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private UserRepository userRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllParents() {
		logger.info("ParentController - getAllParents - starts.");
		return new ResponseEntity<Iterable<ParentEntity>>(parentRepository.findAll(), HttpStatus.OK);
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findParentById(@PathVariable Integer id) {
		logger.info("ParentController - findParentById - starts.");
		try {
			ParentEntity parent = parentRepository.findById(id).orElse(null);

			if (parent == null) {
				logger.info("ParentController - findParentById - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("ParentController - findParentById - finished.");
				return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("ParentController - findParentById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//verzija sa komentarima	
	@Secured("ROLE_ADMIN")
	//@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewParent1(@Valid @RequestBody ParentDto newParent, BindingResult result) {
		logger.info("ParentController - addNewParent - starts.");
		try {
			if(result.hasErrors()) {
				logger.info("ParentController - addNewParent - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
	
			if (newParent == null) {
				logger.info("ParentController - addNewParent - parent object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Parent object is invalid."),HttpStatus.BAD_REQUEST);
			}

//da bih dodao roditlje proveravam da li je neki učenik njegovo dete:
			List<StudentEntity> students = new ArrayList<>();
			for(Integer studentId : newParent.getStudentsId()) {	
				StudentEntity student = studentRepository.findById(studentId).orElse(null);
				if(student != null) {
					students.add(student);
				}
			}
		
			if(students.isEmpty()) {
				logger.info("ParentController - addNewParent - list of students is empty.");				
				return new ResponseEntity<RESTError>(new RESTError("List of student object is invalid."),HttpStatus.BAD_REQUEST);		
			}	
		
			if (newParent.getFirstName() == null || newParent.getLastName() == null || newParent.getDateOfBirth() == null
					|| newParent.getJmbg() == null || newParent.getEmail() == null 
					|| newParent.getUsername() == null || newParent.getPassword() == null) {
				logger.info("ParentController - addNewParent - parent object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Parent object is invalid."),HttpStatus.BAD_REQUEST);
			}		

//********* ako već posotji kao user:
			UserEntity user = userRepository.findByJmbg(newParent.getJmbg());
			if(user != null) {
				ParentEntity parentExists = parentRepository.findById(user.getUserId()).orElse(null);
				if(parentExists != null) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("Parent with the same jmbg already exists."),HttpStatus.BAD_REQUEST);
				}
				if(!( user.getFirstName().equals(newParent.getFirstName()) && user.getLastName().equals(newParent.getLastName()))) {//&& user.getDateOfBirth().equals(newAdmin.getDateOfBirth()))) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");					
					return new ResponseEntity<RESTError>(new RESTError("There is another person with the same jmbg."),HttpStatus.BAD_REQUEST);	
				}
				StudentEntity studentt = studentRepository.findById(user.getUserId()).orElse(null);
				if(studentt != null) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("There is already a student with same jmbg, and the student cannot have another role at the same time."),HttpStatus.BAD_REQUEST);
				}
			
				try {
					parentRepository.insertNewParent(newParent.getEmail(), user.getUserId());
				}
				catch(Exception e){
					logger.info("ParentController - addNewParent - insert query is incorect.");
					return new ResponseEntity<RESTError>(new RESTError("Query is invalid"),HttpStatus.BAD_REQUEST);
				}
/*			ParentEntity parent = new ParentEntity();
			parent.setFirstName(newParent.getFirstName());
			parent.setLastName(newParent.getLastName());
			parent.setJmbg(newParent.getJmbg());
			parent.setEmail(newParent.getEmail());
			parent.setDateOfBirth(newParent.getDateOfBirth());
			parent.setStudents(students);
*/			
						
				try {	
					accountDao.createAndSaveAccount(newParent.getUsername(), newParent.getPassword(), user, EUserRole.ROLE_PARENT);
				
				}
// da li da brišem roditelja ako ne uspe kreiranje accounta ili da napišem kod koji će kreirati samo roditelja pa reći da account nije kreiran i da treba ručno kreirati account?:
				catch (Exception e) {
					parentRepository.deleteById(user.getUserId());
					logger.info("ParentController - addNewParent - account and parent not recorded.");
					return new ResponseEntity<RESTError>(new RESTError("Account for second role not created and parent not added"),	HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
				for(StudentEntity student : students) {
					parentRepository.insertNewParentStudent(user.getUserId(), student.getUserId());
				}
				logger.info("ParentController - addNewParent - finished.");
				return new ResponseEntity<>("Parent was recorded", HttpStatus.OK);	

			} // i ovo obrisati	
			
			
/*			//obrisati
			
		ParentEntity parentSecondRole = parentRepository.findByJmbg(newParent.getJmbg());

			if(parentSecondRole == null) 
//				return new ResponseEntity<RESTError>(new RESTError("Ne postoji taj novi roditelj sa id-jem (starog)usera."),
//						HttpStatus.NOT_FOUND);
				return new ResponseEntity<RESTError>(new RESTError("Ne postoji taj novi roditelj sa jmbg."),
						HttpStatus.NOT_FOUND);
			for(StudentEntity student: students) {
				student.getParents().add(parentSecondRole);
			}
			studentRepository.saveAll(students);
/*
//već provereno gore da li je lista dece prazna, pa je višak:
			if(parent.getStudents().isEmpty()){
				parentRepository.deleteById(parent.getUserId());
				return new ResponseEntity<RESTError>(new RESTError("Parent not added, nema dece"),	HttpStatus.BAD_REQUEST);
			}
//ovde bespotrebno jer već imam tog usera gore:			
			UserEntity userP = (UserEntity)parent;
			s
ovde zatvoriti kosa crta zvezda i obrisati179 i 216 iz linije 			
			try {	
				accountDao.createAndSaveAccount(newParent.getUsername(), newParent.getPassword(), user, EUserRole.ROLE_PARENT);
			}
	// da li da brišem roditelja ako ne uspe kreiranje accounta ili da napišem kod koji će kreirati samo
	// roditelja pa reći da account nije kreiran i da treba ručno kreirati account?:
			catch (Exception e) {
				parentRepository.deleteById(user.getUserId());
				return new ResponseEntity<RESTError>(new RESTError("Account for second role not created and parent not added"),	HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			
			logger.info("ParentController - addNewParent - finished.");
			return new ResponseEntity<ParentEntity>(parentSecondRole, HttpStatus.OK);	
		
		}
	*/	
			else {
		
//*********		
		
				ParentEntity parent = new ParentEntity();
				parent.setFirstName(newParent.getFirstName());
				parent.setLastName(newParent.getLastName());
				parent.setJmbg(newParent.getJmbg());
				parent.setEmail(newParent.getEmail());
				parent.setDateOfBirth(newParent.getDateOfBirth());
				parent.setStudents(students);

				parentRepository.save(parent);
		
/*	profesor reče ne treba, automatski se to odradi:	
// dodavanje tog roditelja deci:
		for( StudentEntity student : parent.getStudents()) {
			try {
				student.getParents().add(parent);
				studentRepository.save(student);
			}
			catch(Exception e){
				parent.getStudents().remove(parent.getStudents().indexOf(student));
				parentRepository.save(parent);
			}
		}
*/
				UserEntity userP = (UserEntity)parent;
		
				try {	
					accountDao.createAndSaveAccount(newParent.getUsername(), newParent.getPassword(), userP, EUserRole.ROLE_PARENT);
				}
// da li da brišem roditelja ako ne uspe kreiranje accounta ili da napišem kod koji će kreirati samo roditelja pa reći da account nije kreiran i da treba ručno kreirati account?:
				catch (Exception e) {
					parentRepository.deleteById(user.getUserId());
					logger.info("ParentController - addNewParent - account not created and parent not recorded.");
					return new ResponseEntity<RESTError>(new RESTError("Account not created and parent not added."),	HttpStatus.INTERNAL_SERVER_ERROR);
				}
/*	ili ovako:	
		catch (Exception e) {
			return new ResponseEntity<RESTError>(new RESTError("parent added but account not created, create one later"),	HttpStatus.INTERNAL_SERVER_ERROR);
		}
*/
		

				logger.info("ParentController - addNewParent - finished.");
				return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);	
				}  // od else!!!
			}catch (Exception e){
				logger.info("ParentController - addNewParent - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);						
		}

	}
	
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewParent(@Valid @RequestBody ParentDto newParent, BindingResult result) {
		logger.info("ParentController - addNewParent - starts.");
		try {
			if(result.hasErrors()) {
				logger.info("ParentController - addNewParent - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
	
			if (newParent == null) {
				logger.info("ParentController - addNewParent - parent object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Parent object is invalid."),HttpStatus.BAD_REQUEST);
			}

			//da bih dodao roditlje proveravam da li je neki učenik njegovo dete:
			List<StudentEntity> students = new ArrayList<>();
			for(Integer studentId : newParent.getStudentsId()) {	
				StudentEntity student = studentRepository.findById(studentId).orElse(null);
				if(student != null) {
					students.add(student);
				}
			}
		
			if(students.isEmpty()) {
				logger.info("ParentController - addNewParent - list of students is empty.");				
				return new ResponseEntity<RESTError>(new RESTError("List of student object is invalid."),HttpStatus.BAD_REQUEST);		
			}	
		
			if (newParent.getFirstName() == null || newParent.getLastName() == null || newParent.getDateOfBirth() == null
					|| newParent.getJmbg() == null || newParent.getEmail() == null 
					|| newParent.getUsername() == null || newParent.getPassword() == null) {
				logger.info("ParentController - addNewParent - parent object is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Parent object is invalid."),HttpStatus.BAD_REQUEST);
			}		

//*ako već postoji kao user:
			UserEntity user = userRepository.findByJmbg(newParent.getJmbg());
			if(user != null) {
				ParentEntity parentExists = parentRepository.findById(user.getUserId()).orElse(null);
				if(parentExists != null) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("Parent with the same jmbg already exists."),HttpStatus.BAD_REQUEST);
				}
				if(!( user.getFirstName().equals(newParent.getFirstName()) && user.getLastName().equals(newParent.getLastName()))) {//&& user.getDateOfBirth().equals(newAdmin.getDateOfBirth()))) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");					
					return new ResponseEntity<RESTError>(new RESTError("There is another person with the same jmbg."),HttpStatus.BAD_REQUEST);	
				}
				StudentEntity studentt = studentRepository.findById(user.getUserId()).orElse(null);
				if(studentt != null) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("There is already a student with same jmbg, and the student cannot have another role at the same time."),HttpStatus.BAD_REQUEST);
				}
			
				try {
					parentRepository.insertNewParent(newParent.getEmail(), user.getUserId());
				}
				catch(Exception e){
					logger.info("ParentController - addNewParent - insert query is incorect.");
					return new ResponseEntity<RESTError>(new RESTError("Query is invalid"),HttpStatus.BAD_REQUEST);
				}
						
				try {	
					accountDao.createAndSaveAccount(newParent.getUsername(), newParent.getPassword(), user, EUserRole.ROLE_PARENT);
				
				}
				catch (Exception e) {
					parentRepository.deleteById(user.getUserId());
					logger.info("ParentController - addNewParent - account and parent not recorded.");
					return new ResponseEntity<RESTError>(new RESTError("Account for second role not created and parent not added"),	HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
				for(StudentEntity student : students) {
					parentRepository.insertNewParentStudent(user.getUserId(), student.getUserId());
				}
				logger.info("ParentController - addNewParent - finished.");
				return new ResponseEntity<>("Parent was recorded", HttpStatus.OK);	

			} 					
			else {
		
//*********				
				ParentEntity parent = new ParentEntity();
				parent.setFirstName(newParent.getFirstName());
				parent.setLastName(newParent.getLastName());
				parent.setJmbg(newParent.getJmbg());
				parent.setEmail(newParent.getEmail());
				parent.setDateOfBirth(newParent.getDateOfBirth());
				parent.setStudents(students);

				parentRepository.save(parent);
		
				UserEntity userP = (UserEntity)parent;
		
				try {	
					accountDao.createAndSaveAccount(newParent.getUsername(), newParent.getPassword(), userP, EUserRole.ROLE_PARENT);
				}
				catch (Exception e) {
					parentRepository.deleteById(user.getUserId());
					logger.info("ParentController - addNewParent - account not created and parent not recorded.");
					return new ResponseEntity<RESTError>(new RESTError("Account not created and parent not added."),	HttpStatus.INTERNAL_SERVER_ERROR);
				}

				logger.info("ParentController - addNewParent - finished.");
				return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);	
				} 
			}catch (Exception e){
				logger.info("ParentController - addNewParent - internal server error.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);						
		}

	}
	
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}
	
	@Secured({"ROLE_ADMIN","ROLE_PARENT"})
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateParent(@Valid @RequestBody ParentDto updateParent, BindingResult result,	@PathVariable Integer id) {
		logger.info("ParentController - updateParent - starts.");
		try {
			if (result.hasErrors()) {
				logger.info("ParentController - updateParent - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			ParentEntity parent = parentRepository.findById(id).orElse(null);

			if (parent == null || updateParent == null) {
				logger.info("ParentController - updateParent - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),HttpStatus.NOT_FOUND);
			}

			if (StringUtils.isNotBlank(updateParent.getFirstName())) {
				parent.setFirstName(updateParent.getFirstName());
			}
			if (StringUtils.isNotBlank(updateParent.getLastName())) {
				parent.setLastName(updateParent.getFirstName());
			}
			if (StringUtils.isNotBlank(updateParent.getJmbg())) {
				parent.setJmbg(updateParent.getJmbg());
			}
			if (StringUtils.isNotBlank(updateParent.getEmail())) {
				parent.setEmail(updateParent.getEmail());
			}

			if (updateParent.getDateOfBirth() != null) {
				parent.setDateOfBirth(updateParent.getDateOfBirth());
			}

			parentRepository.save(parent);

			logger.info("ParentController - updateParent - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("ParentController - updateParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	// dodavanje deteta:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/add-student/{studentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addChildToParent(@PathVariable Integer id, @PathVariable Integer studentId) {
		logger.info("ParentController - addChildToParent - starts.");
		try {
			ParentEntity parent = parentRepository.findById(id).orElse(null);
			StudentEntity student = studentRepository.findById(studentId).orElse(null);
			if (parent == null || student == null) {
				logger.info("ParentController - addChildToParent - not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent or student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if (parent.getStudents().contains(student)) {
				logger.info("ParentController - addChildToParent - parent already has that student.");
				return new ResponseEntity<RESTError>(new RESTError("The student already appears in the list of students."), HttpStatus.BAD_REQUEST);
			}
			parent.getStudents().add(student);
			parentRepository.save(parent);

			logger.info("ParentController - addChildToParent - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);

		} catch (Exception e) {
			logger.info("ParentController - addChildToParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	
	
//brisanje (ne dozvoljava account)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteParent(@PathVariable Integer id) {
		logger.info("ParentController - deleteParent - starts.");
		try {
			ParentEntity parent = parentRepository.findById(id).orElse(null);

			if (parent == null) {
				logger.info("ParentController - deleteParent - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			// provera da li je roditelj nekom učeniku:
			Iterable<StudentEntity> students = studentRepository.findAll();
			for (StudentEntity student : students) {
				if (student.getParents().contains(parent)) {
					logger.info("ParentController - deleteParent - parent cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("A parent cannot be deleted because a student has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}

			// provera da li je to korisnik nekog naloga:
			if (accountRepository.findByUserAndRole((UserEntity) parent, EUserRole.ROLE_PARENT) != null) {
				logger.info("ParentController - deleteParent - parent cannot be deleted.");
				return new ResponseEntity<RESTError>(new RESTError("The parent have account so you can't delete him."),
						HttpStatus.NOT_FOUND);
			}
			parentRepository.deleteById(id);

			logger.info("ParentController - deleteParent - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
		} 
		catch (Exception e) {
			logger.info("ParentController - deleteParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	
	
	
// brisanje (i parenta i accounta)
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "delete/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteParentAndAccount(@PathVariable Integer id) {
		logger.info("ParentController - deleteParent - starts.");
		try {
			ParentEntity parent = parentRepository.findById(id).orElse(null);

			if (parent == null) {
				logger.info("ParentController - deleteParentAndAccount - parent not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			// provera da li je roditelj nekom učeniku:
			Iterable<StudentEntity> students = studentRepository.findAll();
			for (StudentEntity student : students) {
				if (student.getParents().contains(parent)) {
					logger.info("ParentController - deleteParentAndAccount - parent cannot be deleted.");
					return new ResponseEntity<RESTError>(
							new RESTError("A parent cannot be deleted because a student has a reference to it"),
							HttpStatus.BAD_REQUEST);
				}
			}
			
			AccountEntity account = accountRepository.findByUserAndRole((UserEntity)parent, EUserRole.ROLE_STUDENT);
			UserEntity user = (UserEntity) parent;
			user.getAccounts().remove(account);
			try {
				userRepository.save(user);
			} catch (Exception e) {
				logger.info("ParentController - deleteParentAndAccount - Break on save user after remove account.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				parentRepository.delete(parent);
			} catch (Exception e) {
				logger.info("ParentController - deleteParentAndAccount - Can't delete parent from parentRepository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				accountRepository.delete(account);
			} catch (Exception e) {
				logger.info("ParentController - deleteParentAndAccount - Can't delete account from accountRepository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			//parentRepository.deleteById(id);

			logger.info("ParentController - deleteParentAnd - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("ParentController - deleteParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
			
	
	
	
	
	
	
	
//front
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/by-student/{studentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findParentsByStudenttId(@PathVariable Integer studentId) {
		logger.info("StudentController - findParentsByStudenttId - starts.");
		try {
			StudentEntity student = studentRepository.findById(studentId).orElse(null);

			if (student == null) {
				logger.info("StudentController - findParentsByStudenttId - student not found.");
				return new ResponseEntity<RESTError>(new RESTError("Student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				
				ArrayList<ParentEntity> parents = new ArrayList();
				parents.addAll(student.getParents());
					
				
				logger.info("StudentController - findParentsByStudenttId - finished.");
				return new ResponseEntity<Iterable<ParentEntity>>(parents, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("StudentController - findParentsByStudenttId - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/remove-student/{studentId}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> removeChildToParent(@PathVariable Integer id, @PathVariable Integer studentId) {
		logger.info("ParentController - removeChildToParent - starts.");
		try {
			ParentEntity parent = parentRepository.findById(id).orElse(null);
			StudentEntity student = studentRepository.findById(studentId).orElse(null);
			if (parent == null || student == null) {
				logger.info("ParentController - removeChildToParent - not found.");
				return new ResponseEntity<RESTError>(new RESTError("Parent or student with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
/*			if (parent.getStudents().contains(student)) {
				logger.info("ParentController - removeChildToParent - parent already has that student.");
				return new ResponseEntity<RESTError>(new RESTError("The student already appears in the list of students."), HttpStatus.BAD_REQUEST);
			}
*/
			parent.getStudents().remove(student);
			parentRepository.save(parent);

			logger.info("ParentController - removeChildToParent - finished.");
			return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);

		} catch (Exception e) {
			logger.info("ParentController - removeChildToParent - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	
	
	
	





	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	
// da li je ok:	
	//deaktivacija, na sve veze postaviti CascadeType=Cascade.All:
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
	//@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateParent(@PathVariable Integer id) {
		logger.info("ParentController - deactivateParent - starts.");

		ParentEntity parent = parentRepository.findById(id).orElse(null);

		if (parent == null) {
			return new ResponseEntity<RESTError>(new RESTError("Parent with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		UserEntity user = (UserEntity)parent;
		AccountEntity account = accountRepository.findByUserAndRole(user, EUserRole.ROLE_PARENT);
		account.setIsActive(false);
		accountRepository.save(account);
		parentRepository.deleteById(id);
		
		logger.info("ParentController - deactivateParent - finished.");
		return new ResponseEntity<ParentEntity>(parent, HttpStatus.OK);
	}	
	



}
