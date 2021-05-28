package com.bzbees.hrma.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.JobRepository;
import com.bzbees.hrma.entities.Job;

@Service
public class JobService {

	@Autowired
	JobRepository jobRepo;

	public Job save(Job job) {
		return jobRepo.save(job);
	}

	public List<Job> jobsToSave() {
		List<Job> listJobs = (List<Job>) jobRepo.getjobsToSave();
		return listJobs;
	}
	
	public List<Job> jobsSaved() {
		List<Job> listJobs = (List<Job>) jobRepo.getSavedJobs();
		return listJobs;
	}
	
	public List<Job> getDbJobs() {
		List<Job> listJobs = (List<Job>) jobRepo.getDbJobs();
		return listJobs;
	}
	
	public List<Job> getJobsByPersonId(long personId) {
		List<Job> listJobs = (List<Job>) jobRepo.getSavedJobsByPersonId(personId);
		return listJobs;
	}

	public List<Job> getAll() {
		return (List<Job>) jobRepo.findAll();
	}

	public Job findJobById(long id) {
		return jobRepo.findJobByJobId(id);

	}

	public void deleteJobById(Job job) {
		jobRepo.delete(job);
	}
	
	public List<Job> findJobsByAgencyId(long agencyId) {
		return jobRepo.findJobsByAgencyId(agencyId);
	}
	
	public void flushJobDb() {
		jobRepo.flush();
		return;
	}
	
	public void saveAndFlush(Job job) {
		jobRepo.saveAndFlush(job);
		return;
	}
	
	public List<Job> findJobsPostedByAgencies() {
		return jobRepo.findJobsPostedByAgencies();
	}
	
	public Set<Job> findJobsAddedToListByPersonId (long personId) {
		return jobRepo.getJobsAddToListByPersonId(personId);
	}
	
	public Set<Job> findJobsAppliedByPersonId (long personId) {
		return jobRepo.getJobsAppliedByPersonId(personId);
	}
	
	public Set<Long> findJobsIdAddedToListByPersonId (long personId) {
		return jobRepo.findJobsIdInListByPersonId(personId);
	}
	
	public Set<Long> findJobsIdAppliedToByPersonId (long personId) {
		return jobRepo.findJobsAppliedToByPersonId(personId);
	}
	
	public Set<Long> findApprovedJobsIdsByPersonId (long personId) {
		return jobRepo.findApprovedJobsIdsBypersonId(personId);
	}
	
	public Set<Job> findApprovedJobsByPersonId (long personId) {
		return jobRepo.getJobsApprovedByPersonId(personId);
	}
	
	public Set<Job> getValidDateJobsByPersonId(long personId) {
		return jobRepo.getJobsWithValidDateCandidatesByPersonId(personId);
	}
	
	public Set<Long> findValidDateJobsIdsByPersonId (long personId) {
		return jobRepo.findValidDateJobsIdsBypersonId(personId);
	}
	
	public Set<Job> getRejectedJobsByPersonId(long personId){
		return jobRepo.getJobsRejectedByPersonId(personId);
	}
	
	public Set<Long> findRejectedJobsIdsByPersonId(long personId) {
		return jobRepo.findRejectedJobsIdsBypersonId(personId);
	}
	
	
}
