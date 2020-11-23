package com.iktpreobuka.test.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.test.controllers.util.RESTError;
import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.dto.AccountDto;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.utils.Encryption;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "diary/login")
public class LoginController {

	@Autowired
	private AccountRepository accountRepository;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> getUser(@RequestBody AccountDto account,Principal principal) {
		try {
			String username = principal.getName();
			AccountEntity user = accountRepository.findByUsername(username);
			if (user == null) {
				return new ResponseEntity<RESTError>(new RESTError("Account not found!"), HttpStatus.NOT_FOUND);
			}
			if (Encryption.isEqualToEncodedPass(account.getPassword(), user.getPassword())) {
				return new ResponseEntity<AccountEntity>(user, HttpStatus.OK);
			}
			return new ResponseEntity<RESTError>(new RESTError("usrename or password is not valid"), HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}


}
