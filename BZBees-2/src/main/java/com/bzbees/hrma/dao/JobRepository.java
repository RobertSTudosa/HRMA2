package com.bzbees.hrma.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>{
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id is NULL;")
	public List<Job> getjobsToSave ();
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, the_agency_agency_id "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id is NOT NULL;")
	public List<Job> getSavedJobs ();
	
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
			+ "the_agency_agency_id, last_image_id, job_likes_count "
			+ " FROM jobs "
			+ " left outer join person_jobs ON jobs.job_id = person_jobs.job_id " 
			+ " WHERE person_jobs.person_id = ?1 ;")
	public List<Job> getSavedJobsByPersonId (long personId);
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
			+ "the_agency_agency_id, last_image_id, job_likes_count "
			+ " FROM jobs "
			+ " left outer join person_jobs_in_list ON jobs.job_id = person_jobs_in_list.job_id " 
			+ " WHERE person_jobs_in_list.person_id = ?1 ;")
	public Set<Job> getJobsAddToListByPersonId (long personId);
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
			+ "the_agency_agency_id, last_image_id, job_likes_count "
			+ " FROM jobs "
			+ " left outer join person_jobs_applied ON jobs.job_id = person_jobs_applied.job_id " 
			+ " WHERE person_jobs_applied.person_id = ?1 ;")
	public Set<Job> getJobsAppliedByPersonId (long personId);
	
	
	@Query(nativeQuery= true, value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
			+ "job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
			+ "the_agency_agency_id, last_image_id, job_likes_count "
			+ " FROM jobs ;")
	public List<Job> getDbJobs ();
	
	public Job findJobByJobId(long id);
	
	@Query(nativeQuery= true, 
			value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
					+ "			job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
					+ "			the_agency_agency_id, last_image_id, job_likes_count "
					+ "			FROM jobs "
					+ "WHERE the_agency_agency_id = ?1 ;")
	public List<Job> findJobsByAgencyId(long agencyId);
	
	@Query(nativeQuery = true, 
			value="select jobs.job_id , job_title, company_name, currency, job_private, necessary_documents, "
					+ " job_location, start_date, end_date, responsabilities, salary, currency, working_conditions, skills, tags, "
					+ "	the_agency_agency_id, last_image_id "
					+ "	FROM jobs "
					+ "	WHERE the_agency_agency_id is NOT NULL")
	public List<Job> findJobsPostedByAgencies();
	
	@Query(nativeQuery = true,
			value = "select person_jobs_in_list.job_id "
					+ " FROM person_jobs_in_list "
					+ " WHERE person_id = ?1 ;")
	public Set<Long> findJobsIdInListByPersonId(long personId);
	
	@Query(nativeQuery = true,
			value = "select person_jobs_applied.job_id "
					+ " FROM person_jobs_applied "
					+ " WHERE person_id = ?1 ;")
	public Set<Long> findJobsAppliedToByPersonId(long personId);
		
	
	

}
