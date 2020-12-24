package com.bzbees.hrma.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.User;

@Repository
public interface PersonRepository extends CrudRepository <Person, Long>{

	
		public Person findPersonByPersonId(long id);
		
		@Query(nativeQuery= true, value= "select persons.person_id, first_name, last_name, persons.email, app_status, "
				+ "employment_status, location, current_job, private_currentjob, active_job, work_experience, "
				+ " availability, birth_date, job_wish_desc, total_hours, status_start_date, start_job, end_job "				
				+ " FROM persons "  
				+ " left outer join user_accounts ON  persons.person_id = user_accounts.user_id " 
				+ " WHERE user_accounts.user_id = ?1 ;")
		public Person findPersonFromUserId(long personId);
		
		public Person findPersonByEmailIgnoreCase(String email);
		
		
		

	
	

}
