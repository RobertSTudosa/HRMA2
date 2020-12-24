package com.bzbees.hrma.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.SkillRepository;
import com.bzbees.hrma.entities.Skill;

@Service
public class SkillService {
	
	@Autowired
	SkillRepository skillRepo;
	
	public List<Skill> getSkillsToSave () {
		return skillRepo.getSkillsToSave();
	}
	
	public List<Skill> getSavedSkills () {
		return skillRepo.getSavedSkills();
	}
	
	public Skill save (Skill skill) {
		return skillRepo.save(skill);
	}
	
	
	public List<Skill> getAll () {
				List<Skill> skillList = (List<Skill>) skillRepo.findAll();
				
				
				return skillList;
	}
	
	public List<Skill> getDbSkills () {
		List<Skill> skillList = (List<Skill>) skillRepo.getDbSkills();
		
		
		return skillList;
	}
	
	
	public List<Skill> getSavedSkillsByPersonId (long personId) {
		List<Skill> skillList = (List<Skill>) skillRepo.findSkillsForThePerson(personId);
		
		return skillList;
	}
	
	public void deleteAll() {
		skillRepo.deleteAll();
	}
	
	public Skill findSkillById (long id) {
		return skillRepo.findSkillBySkillId(id);
	}
	
	public Skill findSkillBySkillId (long id) {
		return skillRepo.findBySkillId(id);
	}

	
	public void deleteSkill (Skill skill) {
		skillRepo.delete(skill);
	}
//	
//	public List<Person> getPersons (Person person) {
//		
//		
//	}
	
	

}
