package com.iktpreobuka.test.repositories;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.StudentEntity;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer>{


	
	@Modifying
	@Transactional
	@Query(value="insert into student (date_of_entry, note,student_id, school_class) values ( :dateEntered, :note, :userId, :classId)",nativeQuery = true)
	void insertNewStudent(@Param("dateEntered") Date dateEntered, @Param("note") String note, @Param("userId") Integer userId, @Param("classId") Integer classId);
	

}
