package com.iktpreobuka.test.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.test.entities.ClassEntity;
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.StudentEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;

public interface LectureRepository extends CrudRepository<LectureEntity, Integer> {

	LectureEntity findBySchoolClassAndSubjectAndTeacher(ClassEntity schoolClass, SubjectEntity subject,	TeacherEntity teacher);

	

	List<LectureEntity> findAllByTeacher(TeacherEntity teacher);





	List<LectureEntity> findAllBySchoolClass(ClassEntity depart);



	List<LectureEntity> findAllByTeacherAndSubject(TeacherEntity teacher, SubjectEntity subject);



/*treba li i da li je ok zapis:
	LectureEntity findBySchoolClassAndSubject(ClassEntity schoolClass, SubjectEntity subject);
*/
//	Iterable<LectureEntity> findAllByTeacherAndCurrentYear(Integer teacherId, Boolean fromTheCurrentSchoolAge);

}
