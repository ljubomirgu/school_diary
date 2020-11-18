package com.iktpreobuka.test.services;

import java.util.List;

import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.TeacherEntity;

public interface GradingDao {

//	Iterable<GradingEntity> findByParent(Integer id);

	List<GradingEntity> findByParent(ParentEntity parent);

	Iterable<GradingEntity> findByTeacher(TeacherEntity teacher);

}
