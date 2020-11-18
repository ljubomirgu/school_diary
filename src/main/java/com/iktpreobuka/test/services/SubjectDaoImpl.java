package com.iktpreobuka.test.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.iktpreobuka.test.controllers.util.RESTError;
import com.iktpreobuka.test.entities.LectureEntity;
import com.iktpreobuka.test.entities.SubjectEntity;
import com.iktpreobuka.test.entities.TeacherEntity;
import com.iktpreobuka.test.repositories.LectureRepository;
import com.iktpreobuka.test.repositories.TeacherRepository;

@Service
public class SubjectDaoImpl implements SubjectDao{
/*	
	@Autowired
	private TeacherRepository teacherRepository;
	
	@Autowired
	private LectureRepository lectureRepository;

	@Override
	public List<SubjectEntity> findTeachersSubjects(Integer teacherId, Boolean fromTheCurrentSchoolAge) {
		// TODO Auto-generated method stub
		return null;
	}

	/*

	@Override
	public ResponseEntity<?> findTeachersSubjects(Integer teacherId,Boolean fromTheCurrentSchoolAge) {
		TeacherEntity teacher = teacherRepository.findById(teacherId).orElse(null);
		if(teacher == null)
			return new ResponseEntity<RESTError>(new RESTError("Teacher with provided ID not found"),HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<Iterable<LectureEntity>>(lectureRepository.findAllByTeacherAndCurrentYear(teacherId,fromTheCurrentSchoolAge), HttpStatus.OK);
	}
*/
}
