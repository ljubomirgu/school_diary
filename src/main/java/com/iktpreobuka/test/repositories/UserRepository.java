package com.iktpreobuka.test.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.test.entities.UserEntity;


public interface UserRepository extends CrudRepository<UserEntity, Integer>{

	UserEntity findByJmbg(String jmbg);

}
