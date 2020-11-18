package com.iktpreobuka.test.repositories;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.enumerations.EYear;

public interface ClassRepository extends CrudRepository<ClassEntity, Integer>{

	ClassEntity findByYearAndNumberOfDepartmentAndSchoolYear(YearEntity year,String numberOfDepartment,
			String schoolYear);

	
	
	@Modifying
	@Transactional
	@Query(value="select * from class c left join year y on  c.year=y.year_id left join year_subject ys on y.year_id=ys.year_id"
	+ " left join subject s on ys.subject_id=s.subject_id  where s.subject_id =:subjectId", nativeQuery=true)
	public ArrayList<ClassEntity> findAllBySubjectId(@Param ("subjectId") Integer subjectId);
	
	@Modifying
	@Transactional
	@Query(value="select * from class c left join year y on  c.year=y.year_id left join year_subject ys on y.year_id=ys.year_id"
	+ " left join subject s on ys.subject_id=s.subject_id left join teacher_subject ts on s.subject_id=ts.subject_id left join teacher t on ts.teacher_id=t.teacher_id  where s.subject_id =:subjectId and t.teacher_id=:teacherId", nativeQuery=true)
	public ArrayList<ClassEntity> findAllBySubjectIdAndTeacherId(@Param ("subjectId") Integer subjectId, @Param ("teacherId") Integer userId);
	

	
// kada nisam imao classId u studentDto:
//	ClassEntity findByNumberOfDepartmentAndYear(String numberOfDepartment, EYear year);

}
