package com.bzbees.hrma.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Person;

@Repository
public interface PersonRepository extends CrudRepository <Person, Long>{

	
		public Person findPersonByPersonId(long id);
		
		
		

	
	

}
