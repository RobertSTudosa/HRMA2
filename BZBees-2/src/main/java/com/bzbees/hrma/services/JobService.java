package com.bzbees.hrma.services;

import java.util.List;

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
}
