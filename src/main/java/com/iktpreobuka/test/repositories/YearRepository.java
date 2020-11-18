package com.iktpreobuka.test.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.enumerations.EYear;


public interface YearRepository extends CrudRepository<YearEntity, Integer>{

//	YearEntity findByYear(String year);

 	//ili pretvorim taj String year u EYear year: 
 	YearEntity findByYear(EYear year);


}
