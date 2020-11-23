package com.iktpreobuka.test.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iktpreobuka.test.entities.YearEntity;
import com.iktpreobuka.test.enumerations.EYear;


public interface YearRepository extends CrudRepository<YearEntity, Integer>{


 	YearEntity findByYear(EYear year);


}
