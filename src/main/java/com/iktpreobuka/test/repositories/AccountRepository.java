package com.iktpreobuka.test.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.test.entities.AccountEntity;
import com.iktpreobuka.test.entities.UserEntity;
import com.iktpreobuka.test.enumerations.EUserRole;

public interface AccountRepository extends CrudRepository<AccountEntity, Integer>{
	
	public AccountEntity findByUserAndRole(UserEntity user, EUserRole role);

	public AccountEntity findByUsername(String name);
}
