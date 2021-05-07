package com.bzbees.hrma.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Person;

@Repository
public interface PersonRepository extends CrudRepository <Person, Long>{

	
		public Person findPersonByPersonId(long id);
		
		@Query(nativeQuery= true, value= "select persons.person_id, first_name, last_name, persons.email, app_status, "
				+ "employment_status, location, current_job, private_currentjob, active_job, work_experience, "
				+ " availability, birth_date, job_wish_desc, total_hours, status_start_date, start_job, end_job,  "
				+ " last_img_id, unread_notifs, is_affiliated_to_agency "				
				+ " FROM persons "  
				+ " left outer join user_accounts ON  persons.person_id = user_accounts.user_id " 
				+ " WHERE user_accounts.user_id = ?1 ;")
		public Person findPersonFromUserId(long personId);
		
		public Person findPersonByEmailIgnoreCase(String email);
		
		@Query(nativeQuery= true, value= "select persons.person_id, first_name, last_name, persons.email, app_status, "
				+ "employment_status, location, current_job, private_currentjob, active_job, work_experience, "
				+ " availability, birth_date, job_wish_desc, total_hours, status_start_date, start_job, end_job,  "
				+ "	 last_img_id, unread_notifs, is_affiliated_to_agency "	
				+ " FROM persons "
				+ " left outer join person_jobs_applied ON person_jobs_applied.person_id = persons.person_id "
				+ " WHERE person_jobs_applied.job_id = ?1 ;")
		public Set<Person> personsAppliedToJob(long jobId );
		
		@Modifying
		@Query(nativeQuery= true, value= "DELETE FROM bpeople_BZBees.person_jobs_applied WHERE person_id = ?1 ; ")
		public void deletePersonApplications(long personId);
		
		
		

	
	

}
