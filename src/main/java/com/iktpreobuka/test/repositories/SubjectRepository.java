package com.iktpreobuka.test.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;


public interface SubjectRepository extends CrudRepository<SubjectEntity, Integer>{

	public SubjectEntity findBySubjectName(String subject);

	
	//@Query("select s from SubjectEntity s  join s.teacher_subject ts  join ts.teacher t where t = :teacher")
	//public Iterable<SubjectEntity> findByTeacher(@Param ("teacher") TeacherEntity teacher);

}
