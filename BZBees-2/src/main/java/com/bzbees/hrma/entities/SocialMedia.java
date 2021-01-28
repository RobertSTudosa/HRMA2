package com.bzbees.hrma.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class SocialMedia {
	
	
	@Id()
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "socialmedia_generator")
	@SequenceGenerator(name = "social_generator", sequenceName = "socialmedia_seq", allocationSize = 1)
	private long socialMediaId;
	
	private String name;
	
	private String urlAddress;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
    private Agency agency;
	
	@ManyToOne(fetch = FetchType.LAZY, optional=true)
    private Person person;
	
	public SocialMedia() {
		
	}

	public SocialMedia(String name, String urlAddress, Agency agency, Person person) {
		super();
		this.name = name;
		this.urlAddress = urlAddress;
		this.agency = agency;
		this.person = person;
	}

	public long getSocialMediaId() {
		return socialMediaId;
	}

	public void setSocialMediaId(long socialMediaId) {
		this.socialMediaId = socialMediaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlAddress() {
		return urlAddress;
	}

	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	
	

	
	
}
