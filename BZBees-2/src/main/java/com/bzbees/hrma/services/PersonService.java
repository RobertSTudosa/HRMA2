package com.bzbees.hrma.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.PersonRepository;
import com.bzbees.hrma.entities.Person;

@Service
public class PersonService {
	
	@Autowired 
	PersonRepository persRepo;
	
	public Person save(Person person) {
		return persRepo.save(person);
	}
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public boolean checkAvailability (Person person) {
		

		if (person.getAvailability().after(new Date())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkBirthDate (Person person) {
		
		long nowT = new Date().getTime();
		nowT -= 18*24*60*60*1000;
		Date adult18 = new Date(nowT);
		if (person.getBirthDate().before(adult18)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Person findPersonById (long id) {
		return persRepo.findPersonByPersonId(id);
	}
	
	public List<Person> getAll () {
		return (List<Person>) persRepo.findAll();
		
	}
	
	public Person findPersonByUserId(long userId) {
		return persRepo.findPersonFromUserId(userId);
	}
	
	public Person findPersonByEmail (String email) {
		return persRepo.findPersonByEmailIgnoreCase(email);
	}
	
	public Set<Person> findCandidatesAppliedToJob (long jobId) {
		return persRepo.personsAppliedToJob(jobId);
	}
	
	public void deletePersonApplicationsBypersonId(long personId) {
		persRepo.deletePersonApplications(personId);
	}
	
	public Set<Person> getCandidatesApprovedToJob (long jobId) {
		return persRepo.personsApprovedToJob(jobId);
	}
	
	public Set<Long> getPersonsIdsApprovedToJob (long jobId) {
		return persRepo.personsIdsApprovedToJob(jobId);
	}
	
	public Set<Person> getCandidatesWithValidDateByJobId (long jobId) {
		return persRepo.personsValidDateToJob(jobId);
	}
	
	public Set<Long> getCandidatesIdsWithValidDatesByJobId(long jobId) {
		return persRepo.personsIdsWithValidDateToJob(jobId);
	}
	
	public Set<Person> getCandidatesRejectedByJobId (long jobId) {
		return persRepo.personsRejectedToJob(jobId); 
	}
	
	public Set<Long> getCandidatesIdsRejectedByJobId(long jobId) {
		return persRepo.personsIdsRejectedToJob(jobId); 
	}
	
}
