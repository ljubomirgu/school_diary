package com.iktpreobuka.test.repositories;

import java.sql.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.TeacherEntity;

public interface TeacherRepository extends CrudRepository<TeacherEntity, Integer> {

	
	@Modifying
	@Transactional
	@Query(value="insert into teacher (date_of_employment, vocation, teacher_id) values ( :dateOfEmployment, :vocation, :userId)",nativeQuery = true)
	void insertNewTeacher(@Param("dateOfEmployment") Date dateOfEmployment, @Param("vocation") String vocation, @Param("userId") Integer userId);


	@Modifying
	@Transactional
	@Query(value="insert into teacher_subject (subject_id, teacher_id) values (:subjectId, :teacherId)",nativeQuery = true)
	void insertNewTeacherSubject(@Param("subjectId")Integer subjectId, @Param("teacherId")Integer teacherId);

}
