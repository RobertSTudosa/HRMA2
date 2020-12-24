package com.bzbees.hrma.entities;

import java.io.Serializable;
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
@Table(name="lang")
public class Language implements Serializable {
	
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lang_generator")
	@SequenceGenerator(name = "lang_generator", sequenceName = "lang_seq", allocationSize = 1)
	@Column(name="lang_id")
	private long languageId;
	
	private String name;
	
	
	private String level;
	
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
			fetch = FetchType.LAZY)
	@JoinTable(name="person_lang",
			joinColumns=@JoinColumn(name="lang_id"),
			inverseJoinColumns=@JoinColumn(name="person_id"))
	private Set<Person> persons = new HashSet<>();

	public Language(String name, String level, Set<Person> persons) {
		super();
		this.name = name;
		this.level = level;
		this.persons = persons;
	}
	
	public Language() {
		
	}

	public long getLangId() {
		return languageId;
	}

	public void setLangId(long langId) {
		this.languageId = langId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Set<Person> getPersons() {
		return persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	
	
	

}
