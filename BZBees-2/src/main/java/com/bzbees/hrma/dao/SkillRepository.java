package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Skill;

@Repository
public interface SkillRepository extends CrudRepository <Skill, Long> {
	
	@Query(nativeQuery= true, value="select skills.skill_id , skill_description, skill_name "
			+ "FROM skills "
			+ "left outer join person_skills ON skills.skill_id = person_skills.skill_id " 
			+ " WHERE person_skills.person_id is NULL;")
	public List<Skill> getSkillsToSave ();
	
	@Query(nativeQuery= true, value="select skills.skill_id , skill_description, skill_name "
			+ "FROM skills "
			+ "left outer join person_skills ON skills.skill_id = person_skills.skill_id " 
			+ " WHERE person_skills.person_id is NOT NULL;")
	public List<Skill> getSavedSkills ();
	
	@Query(nativeQuery= true, value="select skills.skill_id , skill_description, skill_name "
			+ "FROM skills ")
	public List<Skill> getDbSkills ();
	
	
	@Query(nativeQuery= true, value="select skills.skill_id , skill_description, skill_name "
			+ "FROM skills "
			+ "left outer join person_skills ON skills.skill_id = person_skills.skill_id " 
			+ " WHERE person_skills.person_id = ?1 ;")
	public List<Skill> findSkillsForThePerson (long personId);
	
	public Skill findSkillBySkillId(long id);

}
