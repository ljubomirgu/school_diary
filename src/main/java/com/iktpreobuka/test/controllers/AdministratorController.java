package com.iktpreobuka.test.controllers;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import com.iktpreobuka.test.entities.AdministratorEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.AdministratorDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.repositories.AdministratorRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.services.AccountDao;
@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(value = "diary/administrators")
public class AdministratorController {
	
	@Autowired
	private AdministratorRepository administratorRepository;	
	@Autowired
	private UserRepository userRepository;	
	@Autowired
	private StudentRepository studentRepository;	
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private AccountRepository accountRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllAdministratorsAdmin(Principal principal) {
		logger.info("AdministratorController - findAll - starts");
		return new ResponseEntity<Iterable<AdministratorEntity>>(administratorRepository.findAll(), HttpStatus.OK);
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findAdministratorById(@PathVariable Integer id) {
		logger.info("AdministratorController - findAdministratorById - starts.");
		try {
			AdministratorEntity admin = administratorRepository.findById(id).orElse(null);

			if (admin == null) {
				logger.info("AdministratorController - findAdministratorById - administartor not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("AdministratorController - findAdministratorById - finished.");
				return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("AdministratorController - findAdministratorById - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewAdministrator(@Valid @RequestBody AdministratorDto newAdmin, BindingResult result) {
		logger.info("AdministratorController - addNewAdministrator - starts.");
		try {
			if(result.hasErrors()) {
				logger.info("AdministratorController - addNewAdministrator - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
	
			if (newAdmin == null) {
				logger.info("AdministratorController - addNewAdministrator - administrator is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator object is invalid."),
					HttpStatus.BAD_REQUEST);
			}

			if (newAdmin.getFirstName() == null || newAdmin.getLastName() == null
					|| newAdmin.getJmbg() == null || newAdmin.getDateOfBirth() == null|| newAdmin.getPhoneNumber()== null
					|| newAdmin.getUsername() == null || newAdmin.getPassword() == null) {
				logger.info("AdministratorController - addNewAdministrator - administrator is invalid.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator object is invalid."),
						HttpStatus.BAD_REQUEST);
			}	
			
			if(accountRepository.findByUsername(newAdmin.getUsername()) != null) {
				logger.info("AdministratorController - addNewAdministrator - username already exists.");
				return new ResponseEntity<RESTError>(new RESTError("That usernam already exists."),HttpStatus.BAD_REQUEST);
			}
		
			UserEntity user = userRepository.findByJmbg(newAdmin.getJmbg());
			if(user != null) {
				
				AdministratorEntity administratorExists = administratorRepository.findById(user.getUserId()).orElse(null);
			
				if(administratorExists != null) {
					logger.info("AdministratorController - addNewAdministrator - there exists administrator whit that jmbg.");
					return new ResponseEntity<RESTError>(new RESTError("Administrator with that jmbg already exists."),HttpStatus.BAD_REQUEST);
				}
				if(!( user.getFirstName().equals(newAdmin.getFirstName()) && user.getLastName().equals(newAdmin.getLastName()))) {//&& user.getDateOfBirth().equals(newAdmin.getDateOfBirth()))) {
					logger.info("AdministratorController - addNewAdministrator - someone else has that jmbg.");
					return new ResponseEntity<RESTError>(new RESTError("There is another person with same jmbg."),HttpStatus.BAD_REQUEST);	
				}
				StudentEntity studentt = studentRepository.findById(user.getUserId()).orElse(null);
				if(studentt != null) {
					logger.info("ParentController - addNewParent - jmbg is invalid.");
					return new ResponseEntity<RESTError>(new RESTError("There is already a student with same jmbg, and the student cannot have another role at the same time."),HttpStatus.BAD_REQUEST);
				}
			
				try {
					administratorRepository.insertNewAdmin(newAdmin.getPhoneNumber(), user.getUserId());
				}
				catch(Exception e){
					logger.info("AdministratorController - addNewAdministrator - insert  in to second table failed.");
					return new ResponseEntity<RESTError>(new RESTError("Query is invalid"),HttpStatus.BAD_REQUEST);

				}				
				try {	
					accountDao.createAndSaveAccount(newAdmin.getUsername(), newAdmin.getPassword(), user, EUserRole.ROLE_ADMIN);				
				}
				catch (Exception e) {
					administratorRepository.deleteById(user.getUserId());
					logger.info("AdministratorController - addNewAdministrator - account for second role not created.");
					return new ResponseEntity<RESTError>(new RESTError("Account for second role not created and administrator not added"),	HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
				logger.info("AdministratorController - addNewAdministrator - finished.");
				return new ResponseEntity<>("Administrator was successfully recorded", HttpStatus.OK);	
		
			}	
			
			AdministratorEntity admin = new AdministratorEntity();
			admin.setFirstName(newAdmin.getFirstName());
			admin.setLastName(newAdmin.getLastName());
			admin.setJmbg(newAdmin.getJmbg());
			admin.setDateOfBirth(newAdmin.getDateOfBirth());
			admin.setPhoneNumber(newAdmin.getPhoneNumber());

			administratorRepository.save(admin);

			UserEntity userAcc = (UserEntity)admin;
			try {	
				accountDao.createAndSaveAccount(newAdmin.getUsername(), newAdmin.getPassword(), userAcc, EUserRole.ROLE_ADMIN);
			}
			catch (Exception e) {
				administratorRepository.deleteById(user.getUserId());
				logger.info("AdministratorController - addNewAdministrator - account not created and administrator is deleted.");
				return new ResponseEntity<RESTError>(new RESTError("Account not created and administrator is deleted."),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
	
			logger.info("AdministratorController - addNewAdministrator - finished.");
			return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("AdministratorController - addNewAdministrator - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(" "));
		}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> updateAdministrator(@PathVariable Integer id, @Valid @RequestBody AdministratorDto updateAdmin, BindingResult result) {
		logger.info("AdministratorController - updateUser - starts.");
		try {
			if(result.hasErrors()) {
				logger.info("AdministratorController - updateUser - validation error.");
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			AdministratorEntity admin = administratorRepository.findById(id).orElse(null);

			if (admin == null || updateAdmin == null) {
				logger.info("AdministratorController - updateUser - administrator not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administartor with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			if(updateAdmin.getFirstName() == null && updateAdmin.getLastName() == null && updateAdmin.getJmbg() == null
					&& updateAdmin.getPhoneNumber() == null  && updateAdmin.getDateOfBirth() == null) {
				logger.info("AdministratorController - updateUser - update admin object is invalid error.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator object is invalid."), HttpStatus.BAD_REQUEST);
			}
				
				
			if (updateAdmin.getFirstName() != null && !updateAdmin.getFirstName().equals(" ") && !updateAdmin.getFirstName().equals("")) {
				admin.setFirstName(updateAdmin.getFirstName());
			}
			if (updateAdmin.getLastName() != null && !updateAdmin.getLastName().equals(" ") && !updateAdmin.getLastName().equals("")) {
				admin.setLastName(updateAdmin.getLastName());
			}

			if (updateAdmin.getJmbg() != null && !updateAdmin.getJmbg().equals(" ") && !updateAdmin.getJmbg().equals("")) {
				admin.setJmbg(updateAdmin.getJmbg());
			}
	
			if (updateAdmin.getDateOfBirth() != null) {
				admin.setDateOfBirth(updateAdmin.getDateOfBirth());
			}
		
			if (updateAdmin.getPhoneNumber() != null && !updateAdmin.getPhoneNumber().equals(" ") && !updateAdmin.getPhoneNumber().equals("")) {
				admin.setPhoneNumber(updateAdmin.getPhoneNumber());
			}
	
			administratorRepository.save(admin);
		
			logger.info("AdministratorController - updateUser - finished.");
			return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
		}
		catch (Exception e){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteAdministrator(@PathVariable Integer id) {
		logger.info("AdministratorController - deleteAdministrator - starts.");
		try {
			AdministratorEntity admin = administratorRepository.findById(id).orElse(null);

			if (admin == null) {
				logger.info("AdministratorController - deleteAdministrator - administartor not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
		
			if(accountRepository.findByUserAndRole((UserEntity)admin, EUserRole.ROLE_ADMIN)!=null) {
				logger.info("AdministratorController - deleteAdministrator - the administartor have account so cannot be deleted.");		
				return new ResponseEntity<RESTError>(new RESTError("There is  account whose user is this administartor so you can't delete him."),HttpStatus.BAD_REQUEST);
			}
			administratorRepository.deleteById(id);
		
			logger.info("AdministratorController - deleteAdministrator - finished.");		
			return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
		}
		catch (Exception e){
			logger.info("AdministratorController - deleteAdministrator - internal server error.");		
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteAdministratorAndAccount(@PathVariable Integer id) {
	logger.info("AdministratorController - deleteAdministratorAndAccount - starts.");
	try {
			AdministratorEntity admin = administratorRepository.findById(id).orElse(null);

			if (admin == null) {
				logger.info("AdministratorController - deleteAdministratorAndAccount - administartor not found.");
				return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			AccountEntity account = accountRepository.findByUserAndRole((UserEntity) admin, EUserRole.ROLE_ADMIN);
			UserEntity user = (UserEntity) admin;
			user.getAccounts().remove(account);

			try {
				userRepository.save(user);
			} catch (Exception e) {
				logger.info("AdministratorController - deleteAdministratorAndAccount - Break on save user after remove account.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				administratorRepository.delete(admin);
			} catch (Exception e) {
				logger.info("AdministratorController - deleteAdministratorAndAccount - Can't delete admin from administratorRepository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			try {
				accountRepository.delete(account);
			} catch (Exception e) {
				logger.info("AdministratorController - deleteAdministratorAndAccount - Can't delete account from accountRepository.");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			logger.info("AdministratorController - deleteAdministrator - finished.");
			return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("AdministratorController - deleteAdministrator - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method=RequestMethod.GET,value="/downloadLogFile")
	public void getLogFile2(HttpSession session,HttpServletResponse response) throws Exception {
	    try {
	        String filePathToBeServed = "D:\\BRAINS\\SpringWorkspace\\school_diary\\logs\\spring-boot-logging.log";
	        File fileToDownload = new File(filePathToBeServed);
	        InputStream inputStream = new FileInputStream(fileToDownload);
	        response.setContentType("text/json");
	        response.setCharacterEncoding("utf-8");
	        response.setHeader("Content-Disposition", "inline"); 
	        IOUtils.copy(inputStream, response.getOutputStream());
	        response.flushBuffer();
	        inputStream.close();
	        
	    } catch (Exception e){
	        logger.info("Request could not be completed at this moment. Please try again.");
	        e.printStackTrace();
	       }   

	}
	

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
	//@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateAdministrator(@PathVariable Integer id) {
		logger.info("AdministratorController - deactivateAdministrator - starts.");

		AdministratorEntity admin = administratorRepository.findById(id).orElse(null);

		if (admin == null) {
			return new ResponseEntity<RESTError>(new RESTError("Administrator with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		UserEntity user = (UserEntity) admin;
		
		AccountEntity account = accountRepository.findByUserAndRole(user, EUserRole.ROLE_ADMIN);
		account.setIsActive(false);
		accountRepository.save(account);
		
		logger.info("AdministratorController - deactivateAdministrator - finished.");
		return new ResponseEntity<AdministratorEntity>(admin, HttpStatus.OK);
	}

	
}
