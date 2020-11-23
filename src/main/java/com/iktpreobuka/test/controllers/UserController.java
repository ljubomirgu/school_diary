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
