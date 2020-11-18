package com.iktpreobuka.test.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import com.iktpreobuka.test.entities.GradingEntity;
import com.iktpreobuka.test.entities.ParentEntity;
import com.iktpreobuka.test.entities.TeacherEntity;

@Service
public class GradingDaoImpl implements GradingDao{

	@PersistenceContext
	private EntityManager em;


	@Override
	public List<GradingEntity> findByParent(ParentEntity parent) {
		String sql = "select g " +
				"from GradingEntity g " +
				"left join fetch g.student s left join fetch s.parents p" +
				"where p = :parent ";
		
				Query query = em.createQuery(sql);
				query.setParameter("parent", parent);
				List<GradingEntity> result = new ArrayList<>();
				result = query.getResultList();
			return result;

	}


	@Override
	public Iterable<GradingEntity> findByTeacher(TeacherEntity teacher) {
		String sql = "select g " +
				"from GradingEntity g " +
				"left join fetch g.lecture l" +
				"where l.teacher = :teacher ";
		
				Query query = em.createQuery(sql);
				query.setParameter("teacher", teacher);
				List<GradingEntity> result = new ArrayList<>();
				result = query.getResultList();
			return result;
	}
	
	
}
