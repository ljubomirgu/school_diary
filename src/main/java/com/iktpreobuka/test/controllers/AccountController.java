package com.iktpreobuka.test.controllers;

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
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.test.controllers.util.RESTError;
import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.entities.dto.AccountDto;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;/*
import com.iktpreobuka.test.repositories.AdministratorRepository;
import com.iktpreobuka.test.repositories.ParentRepository;
import com.iktpreobuka.test.repositories.StudentRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;*/
import com.iktpreobuka.test.repositories.UserRepository;
import com.iktpreobuka.test.security.Views;
import com.iktpreobuka.test.utils.Encryption;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping(value = "diary/accounts")
public class AccountController {
	
	
	@Autowired
	private AccountRepository accountRepository;	
	@Autowired
	private UserRepository userRepository;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> getAllAccounts() {
		logger.info("AccountController - getAllAccounts - starts.");
		return new ResponseEntity<Iterable<AccountEntity>>(accountRepository.findAll(), HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> findAccountById(@PathVariable Integer id) {
		logger.info("AccountController - findAccountById- starts.");
		try {
			AccountEntity account = accountRepository.findById(id).orElse(null);

			if (account == null) {
				logger.info("AccountController - getAllAccounts - account not found.");
				return new ResponseEntity<RESTError>(new RESTError("Account with provided ID not found."),
						HttpStatus.NOT_FOUND);
			} else {
				logger.info("AccountController - getAllAccounts - finished.");
				return new ResponseEntity<AccountEntity>(account, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("AccountController - getAllAccounts - intrenal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> addNewAccount(@Valid @RequestBody AccountDto newAccount, BindingResult result) {
		logger.info("AccountController - addNewAccount - start.");
		
		if (result.hasErrors()) {
			logger.error("AccountController - addNewAccount - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} 
 
 		if(newAccount == null) {
 			logger.info("AccountController - addNewAccount - new account is null.");
			return new ResponseEntity<RESTError>(new RESTError("Account object is invalid."),HttpStatus.BAD_REQUEST);
 		}
 		if(newAccount.getPassword()==null || newAccount.getUsername()==null || newAccount.getRole()==null || newAccount.getUserId()==null)
			return new ResponseEntity<RESTError>(new RESTError("Incorected role"),HttpStatus.BAD_REQUEST);
 			
 		if(!(newAccount.getRole().equals(EUserRole.ROLE_ADMIN.toString()) || newAccount.getRole().equals(EUserRole.ROLE_TEACHER.toString())
 				|| newAccount.getRole().equals(EUserRole.ROLE_STUDENT.toString()) || newAccount.getRole().equals(EUserRole.ROLE_PARENT.toString())))
			return new ResponseEntity<RESTError>(new RESTError("Incorected role"),HttpStatus.BAD_REQUEST);


 		AccountEntity account = new AccountEntity();
		account.setUsername(newAccount.getUsername());
		account.setPassword(Encryption.getPassEncoded(newAccount.getPassword()));
		
		account.setUser(null);

		account.setRole(EUserRole.valueOf(newAccount.getRole()));
		account.setIsActive(true);
		accountRepository.save(account);
		
		logger.info("AccountController - getAllAccounts - finished.");
		return new ResponseEntity<>(newAccount, HttpStatus.OK);
	}
	
	private String createErrorMessage(BindingResult result) {
		return result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(" "));
	}


	@Secured("ROLE_ADMIN")
//	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<?> updateAccountsUsePass(@PathVariable Integer id,
			@Valid @RequestBody AccountDto updateAccount, BindingResult result) {
		logger.info("AccountController - updateAccountsUsePass - starts.");
		
		if (result.hasErrors()) {
			logger.error("AccountController - updateAccountsUsePass - validation error.");
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} 
		try {
			AccountEntity account = accountRepository.findById(id).orElse(null);
	
			if (account == null) {
				logger.info("AccountController - updateAccountsUsePass - account not found.");
				return new ResponseEntity<RESTError>(new RESTError("Account with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}
			if(updateAccount.getRole()!=null || updateAccount.getUserId()!= null)
				return new ResponseEntity<RESTError>(new RESTError("You can only change username and/or password."),HttpStatus.BAD_REQUEST);

					
			if (StringUtils.isNotBlank(updateAccount.getUsername())) {
				account.setUsername(updateAccount.getUsername());
			}

			if (StringUtils.isNotBlank(updateAccount.getPassword())) {
				account.setPassword(Encryption.getPassEncoded(updateAccount.getPassword()));
			}
	
			accountRepository.save(account);

			logger.info("AccountController - updateAccountsUsePass - starts.");
			return new ResponseEntity<AccountEntity>(account, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.info("AccountController - updateAccountsUsePass - intrenal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/deactivate/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deactivateAccount(@PathVariable Integer id) {
		logger.info("AccountController - deactivateAccount - starts.");
		try {
			AccountEntity account = accountRepository.findById(id).orElse(null);

			if (account == null) {
				logger.info("AccountController - deactivateAccount - account not found.");
				return new ResponseEntity<RESTError>(new RESTError("Account with provided ID not found."),
						HttpStatus.NOT_FOUND);
			}

			account.setIsActive(false);
			accountRepository.save(account);

			logger.info("AccountController - deactivateAccount - finished.");
			return new ResponseEntity<AccountEntity>(account, HttpStatus.OK);
		} catch (Exception e) {
			logger.info("AccountController - deactivateAccount - internal server error.");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
//	@JsonView(Views.Admin.class)
	public ResponseEntity<?> deleteAccount(@PathVariable Integer id) {
		logger.info("AccountController - deleteAccount - starts.");

		AccountEntity account = accountRepository.findById(id).orElse(null);

		if (account == null) {
			return new ResponseEntity<RESTError>(new RESTError("Account with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		Iterable<UserEntity> users = userRepository.findAll();
		for (UserEntity user : users) {
			if (user.getAccounts().contains(account))
				logger.info("AccountController - deleteAccount - someone uses this account so it cannot be deleted.");
				return new ResponseEntity<RESTError>(new RESTError("There is a user whose account is this so you can't delete it.."),
						HttpStatus.BAD_REQUEST);
		}
		
		accountRepository.deleteById(id);
	
		logger.info("AccountController - deleteAccount - finished.");
		return new ResponseEntity<AccountEntity>(account, HttpStatus.OK);
	
	}
}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	









	
	
// treba li ovde PUT za izmenu role?:
// svodi se na deaktivaciju tog accounta i pravljenje novog za tu novu rolu
/*
	@RequestMapping(method = RequestMethod.PUT, value = "change/{id}/role/{roleId}")
	public ResponseEntity<?> changeAccountRole(@PathVariable Integer id, @PathVariable Integer roleId) {
		AccountEntity account = accountRepository.findById(id).get();
		RoleEntity role = roleRepository.findById(roleId).get();

		if (account == null) {
			return new ResponseEntity<RESTError>(new RESTError("Account with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
		
		if (role == null) {
			return new ResponseEntity<RESTError>(new RESTError("Role with provided ID not found."),
					HttpStatus.NOT_FOUND);
		}
	//	EUserRole userRole = EUserRole.valueOf(role);
		account.setRole(role);

		return new ResponseEntity<AccountEntity>(accountRepository.save(account), HttpStatus.OK);
	}

}
	*/