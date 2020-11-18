package com.iktpreobuka.test.controllers;

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
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.UserDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(path = "diary/users")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllUsers() {
		logger.info("UserController - getAllUsers - starts.");		
		Iterable<UserEntity> users = userRepository.findAll();
		
		logger.info("UserController - getAllUsers - finished.");
		return new ResponseEntity<Iterable<UserEntity>>(users, HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findUsreById(@PathVariable Integer id) {
		logger.info("UserController - findTeacherById - starts.");
		try {
			UserEntity user = userRepository.findById(id).orElse(null);

			if (user == null) {
				logger.info("UserController - findUsreById - user not found.");
				return new ResponseEntity<RESTError>(new RESTError("User with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("UserController - findUsreById - finished.");
				return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("UserController - findUsreById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

























/*
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findUserById(@PathVariable Integer id) {
		UserEntity user = userRepository.findById(id).get();

		if (user == null) {
			return new ResponseEntity<RESTError>(new RESTError("User with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		UserEntity user = userRepository.findById(id).get();

		if (user == null) {
			return new ResponseEntity<RESTError>(new RESTError("User with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		userRepository.deleteById(id);
		return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewUser(@Valid @RequestBody UserDto newUser, BindingResult result) {

		if(result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		}
	
		if (newUser == null) {
			return new ResponseEntity<RESTError>(new RESTError("User object is invalid."),
					HttpStatus.BAD_REQUEST);
		}

		if (newUser.getFirstName() == null || newUser.getLastName() == null || newUser.getRole() == null
				|| newUser.getJmbg() == null || newUser.getDateOfBirth() == null) {
			return new ResponseEntity<RESTError>(new RESTError("User object is invalid."),
					HttpStatus.BAD_REQUEST);
		}		
			
		UserEntity user = new UserEntity();
		user.setFirstName(newUser.getFirstName());
		user.setLastName(newUser.getLastName());
		user.setJmbg(newUser.getJmbg());
		user.setDateOfBirth(newUser.getDateOfBirth());
		
// šta sa ulogom, ako je ovde dodelim napraviću nalsednicu?:
		user.setRole(EUserRole.ROLE_ADMIN);
		
// kako da automatski snima sve nasledne torke i u user tabelu?
		
			
		return new ResponseEntity<UserEntity>(userRepository.save(user), HttpStatus.OK);
	}
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
	}
	
	
}
*/