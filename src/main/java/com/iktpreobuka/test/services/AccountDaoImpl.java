package com.iktpreobuka.test.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.enumerations.EUserRole;
import com.iktpreobuka.test.repositories.AccountRepository;
import com.iktpreobuka.test.utils.Encryption;

@Service
public class AccountDaoImpl implements AccountDao{

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public AccountEntity createAndSaveAccount(String username, String password, UserEntity user, EUserRole role) {
		
		AccountEntity account = new AccountEntity();
		account.setPassword(Encryption.getPassEncoded(password));
		account.setUsername(username);
		account.setUser(user);
		account.setRole(role);
		account.setIsActive(true);
		
		accountRepository.save(account);
		return account;
	}

}
