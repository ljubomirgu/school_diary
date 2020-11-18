package com.iktpreobuka.test.utils;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.iktpreobuka.test.entities.dto.AccountDto;


@Component
public class AccountCustomValidator implements Validator{

	@Override
	public boolean supports(Class<?> myClass) {
		
		return AccountDto.class.equals(myClass);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AccountDto account = (AccountDto) target;
		
	//	if(!account.getPassword().equals(account.getConfirmedPassword())) {
	//		errors.reject("400", "Passwords must be the same!");
		}
		
	}


