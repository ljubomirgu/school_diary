package com.iktpreobuka.test.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.AdministratorEntity;

public interface AdministratorRepository extends CrudRepository<AdministratorEntity, Integer>{


	@Modifying
	@Transactional
	@Query(value="insert into administrator (phone_number, admin_id) values (:phoneNumber, :userId)",nativeQuery = true)
	void insertNewAdmin(@Param("phoneNumber") String phoneNumber, @Param("userId")Integer userId);

	@Modifying
	@Transactional
	@Query(value="delete from administrator where admin_id = :adminId",nativeQuery = true)
	void deleteNewAdmin(@Param("adminId") Integer adminId);

}
