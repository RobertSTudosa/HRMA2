package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Skill;

@Repository
public interface JobRepository extends CrudRepository<Job, Long>{
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id is NULL;")
	public List<Job> getjobsToSave ();
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id is NOT NULL;")
	public List<Job> getSavedJobs ();
	
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id = ?1 ;")
	public List<Job> getSavedJobsByPersonId (long personId);
	
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags "
			+ " FROM jobs ;")
	public List<Job> getDbJobs ();
	
	public Job findJobByJobId(long id);

}
