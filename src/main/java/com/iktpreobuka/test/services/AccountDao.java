package com.iktpreobuka.test.services;

import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.enumerations.EUserRole;

public interface AccountDao {

	public AccountEntity createAndSaveAccount(String username, String password, UserEntity user, EUserRole role);
	
	/*
	public String getLoggedInUsername() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		return auth.getName();

	}
	*/
}
