package com.bzbees.hrma.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="skills")
public class Skill {
	
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skills_generator")
	@SequenceGenerator(name = "skills_generator", sequenceName = "skills_seq", allocationSize = 1)
	@Column(name="skill_id")
	private long skillId;
	

	@Column(name="skill_name")
	private String skillName;
	
	@Column(name="skill_description")
	private String skillDescription;
	


	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_skills",
			joinColumns=@JoinColumn(name="skill_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> persons = new HashSet<>();
	
	public Skill(long skillId, String skillName, Set<Person> persons) {
		super();
		this.skillId = skillId;
		this.skillName = skillName;
		this.persons = persons;
	}
	
	public Skill() {
		
	}
	
	public static boolean isValid (Skill skill) {
		return (skill.getSkillName() != null);
	}

	public long getSkillId() {
		return skillId;
	}

	public void setSkillId(long skillId) {
		this.skillId = skillId;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	
	public String getSkillDescription() {
		return skillDescription;
	}

	public void setSkillDescription(String skillDescription) {
		this.skillDescription = skillDescription;
	}
	
	public void addPerson (Person person) {
		
			persons.add(person);			
		

	}

	
	

}
