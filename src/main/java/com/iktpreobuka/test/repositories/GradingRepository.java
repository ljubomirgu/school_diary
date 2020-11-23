package com.iktpreobuka.test.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.TeacherEntity;

public interface GradingRepository extends CrudRepository<GradingEntity, Integer>  {

	Iterable<GradingEntity> findByStudent(StudentEntity student);

	@Query("select g from GradingEntity g  join g.student s  join s.parents p where p = :parent")
 	public List<GradingEntity> findByParent(@Param ("parent") ParentEntity parent);
		
	public GradingEntity findByStudentAndGradingId(StudentEntity student, Integer gradeId);


	
	@Query("select g from GradingEntity g  join g.lecture l join l.teacher t where t = :teacher")
	public Iterable<GradingEntity> findByTeacher(@Param ("teacher") TeacherEntity teacher);
}
