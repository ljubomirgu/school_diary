package com.iktpreobuka.test.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.ParentEntity;

public interface ParentRepository extends CrudRepository<ParentEntity, Integer> {

	
	@Modifying
	@Transactional
	@Query(value="insert into parent (parent_email, parent_id) values (:email, :userId)",nativeQuery = true)
	void insertNewParent(@Param("email") String email, @Param("userId") Integer userId);

	ParentEntity findByJmbg(String jmbg);
	
	@Modifying
	@Transactional
	@Query(value="insert into parent_student (parent_id, student_id) values (:parentId, :studentId)",nativeQuery = true)
	void insertNewParentStudent(@Param("parentId") Integer parentId, @Param("studentId") Integer studentid);
	
}

